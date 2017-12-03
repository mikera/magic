package magic.compiler;

import java.io.IOException;
import java.util.ArrayList;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

import magic.Keywords;
import magic.Symbols;
import magic.ast.Constant;
import magic.ast.HashMap;
import magic.ast.Lookup;
import magic.ast.Node;
import magic.data.Lists;
import magic.data.Symbol;
import magic.data.Tuple;

/**
 * Parboiled Parser implementation which reads Magic source code and produces a tree of parsed objects.
 * 
 * 
 * @author Mike
 *
 */
@BuildParseTree
public class Reader extends BaseParser<Node<? extends Object>> {

	// OVERALL PARSING INPUT RULES
	
	public Rule Input() {
		return Sequence(
				Optional(WhiteSpace()),
				ExpressionList(),
				Optional(WhiteSpace()),
				EOI
				);
	}
	
	public Rule ExpressionInput() {
		return Sequence(
				Optional(WhiteSpace()),
				Expression(),
				Optional(WhiteSpace()),
				EOI
				);
	}
	
	public Rule SymbolInput() {
		return Sequence(
				Optional(WhiteSpace()),
				Symbol(),
				Optional(WhiteSpace()),
				EOI
				);
	}
	
    public Rule WhiteSpace() {
        return OneOrMore(FirstOf(
        		WhiteSpaceCharacter(),
        		Sequence("#_",Optional(WhiteSpace()),Expression(),drop()), // remember to drop expression from value stack!
        		Sequence(';',OneOrMore(NoneOf("\n")),'\n')));
    }
    
    public Rule WhiteSpaceCharacter() {
        return AnyOf(" \t\f,\r\n");
    }
	
	// EXPRESSIONS
	
	public Rule Expression() {
		return MaybeMeta(FirstOf(
				DelimitedExpression(),
				UndelimitedExpression()
				));
	}
	
	public Rule MaybeMeta(Rule r) {
		return FirstOf(r,
				Sequence(Meta(),
						WhiteSpace(),
						Expression(),
						push(pop().assocMeta(Keywords.META, interpretMetadata(pop())))));
	}
	
	/**
	 * Converts a metadata object according to the following rule:
	 * - Map -> unchanged
	 * - Keyword -> {:keyword true}
	 * - Any other expression -> {:tag expression}
	 * 
	 * @param node
	 * @return
	 */
	public Node<? extends Object> interpretMetadata(Node<? extends Object> node) {
		if (node instanceof magic.ast.HashMap) return node;
		if (node.isKeyword()) return HashMap.create(Tuple.of(node,Constant.TRUE));
		return HashMap.create(Tuple.of(Constant.create(Keywords.TAG),node));
	}

	public Rule Meta() {
		return Sequence("^",
				Expression());
	}
	
	// Delimited expressions don't need separating whitespace after them
	public Rule DelimitedExpression() {
		return FirstOf(
				DataStructure(),
				Quoted(DelimitedExpression())
				);
	}
	
	public Rule UndelimitedExpression() {
		return FirstOf(
				Constant(),
				Keyword(),
				Symbol(),
				Quoted(UndelimitedExpression())
				);
	}

	Action<Object> ListAddAction(Var<ArrayList<Node<Object>>> expVar) {
		return new Action<Object>() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean run(Context<Object> context) {
				Node<Object> o=(Node<Object>) pop();
				if (o==null) throw new Error ("Null object popped????");
				expVar.get().add(o);
				return true;
			}
		};
	}
	
	/**
	 * Rule for an expression list, containing zero or more expressions and optional whitespace
	 * @return
	 */
	public Rule ExpressionList() {
		Var<ArrayList<Node<Object>>> expVar=new Var<>(new ArrayList<>());
		return Sequence(
				Optional(WhiteSpace()),
				ZeroOrMore(Sequence( // initial expressions with following whitespace or delimiter
							FirstOf(Sequence(DelimitedExpression(),Optional(WhiteSpace())),
									Sequence(UndelimitedExpression(),FirstOf(WhiteSpace(),Test(AnyOf("([{"))))),
							ListAddAction(expVar)
						  )),
				Optional(Sequence( // final expression without whitespace
							Expression(),
							ListAddAction(expVar))),
				push(magic.ast.ListForm.create(Lists.create(expVar.get()),getSourceInfo()))
				);
	}
	
	// QUOTING and UNQUOTING
	
	public Rule Quoted(Rule r) {
		return FirstOf(
				Quote(r),
				SyntaxQuote(r),
				Unquote(r), 
				UnquoteSplice(r)
				);
	}
	
	@SuppressWarnings("unchecked")
	public Rule Quote(Rule r) {
		return Sequence(
				'\'',
				r,
				push(magic.ast.ListForm.create(Lists.of(Lookup.create(Symbols.QUOTE),pop()),getSourceInfo()))
				);
	}
	
	@SuppressWarnings("unchecked")
	public Rule SyntaxQuote(Rule r) {
		return Sequence(
				'`',
				r,
				push(magic.ast.ListForm.create(Lists.of(Lookup.create(Symbols.SYNTAX_QUOTE),pop()),getSourceInfo()))
				);
	}
	
	@SuppressWarnings("unchecked")
	public Rule Unquote(Rule r) {
		return Sequence(
				'~',
				r,
				push(magic.ast.ListForm.create(Lists.of(Lookup.create(Symbols.UNQUOTE),pop()),getSourceInfo()))
				);
	}
	
	@SuppressWarnings("unchecked")
	public Rule UnquoteSplice(Rule r) {
		return Sequence(
				"~@",
				r,
				push(magic.ast.ListForm.create(Lists.of(Lookup.create(Symbols.UNQUOTE_SPLICING),pop()),getSourceInfo()))
				);
	}
	
	// DATA TYPE LITERALS
	
	protected magic.ast.ListForm popNodeList() {
		return (magic.ast.ListForm) pop();
	}
	
	public Rule DataStructure() {
		return FirstOf(
				Vector(),
				List(),
				Set(),
				Map());
	}
	
	public Rule Vector() {
		return Sequence(
				'[',
				ExpressionList(),
				']',
				push(magic.ast.Vector.create(popNodeList(),getSourceInfo())));
	}
	
	public Rule List() {
		return Sequence(
				'(',
				ExpressionList(),
				')',
				push(magic.ast.ListForm.create(popNodeList(),getSourceInfo())));
	}
	
	public Rule Set() {
		return Sequence(
				"#{",
				ExpressionList(),
				'}',
				push(magic.ast.ListForm.createCons(Lookup.create(Symbols.SET),popNodeList(),getSourceInfo()))
				);
	}
	
	public Rule Map() {
		return Sequence(
				"{",
				ExpressionList(),
				'}',
				push(magic.ast.ListForm.createCons(Lookup.create(Symbols.HASHMAP),popNodeList(),getSourceInfo()))
				);
	}
	
	// CONSTANT LITERALS
	
	public Rule Constant() {
		return FirstOf(
				NumberLiteral(),
				StringLiteral(),
				NilLiteral(),
				BooleanLiteral(),
				CharLiteral());
	}
	
	public Rule NilLiteral() {
		return Sequence(
				"nil",
				TestNot(FollowingSymbolCharacter()),
				push(Constant.create(null)));
	}
	
	public Rule CharLiteral() {
		return Sequence(
				'\\',
				FirstOf(Sequence("newline",push(Constant.create('\n'))),
						Sequence("space",push(Constant.create(' '))),
						Sequence("tab",push(Constant.create('\t'))),
						Sequence("formfeed",push(Constant.create('\f'))),
						Sequence("backspace",push(Constant.create('\b'))),
						Sequence("return",push(Constant.create('\r'))),
						Sequence("u", 
								NTimes(4,HexDigit()),
								push(magic.ast.Constant.create((char) Long.parseLong(match(), 16),getSourceInfo())))
						));
	}
	
	public Rule BooleanLiteral() {
		return FirstOf(
				Sequence("true",push(Constant.create(Boolean.TRUE))),
				Sequence("false",push(Constant.create(Boolean.FALSE))));
	}
	
	public Rule StringLiteral() {
		StringVar sb=new StringVar("");

		return Sequence(
				'"',
				ZeroOrMore(Sequence(StringCharacter(),sb.append(matchOrDefault("0")))),
				push(magic.ast.Constant.create(sb.get().toString(),getSourceInfo())),
				'"');
	}
	
    public Rule StringCharacter() {
        return FirstOf(NoneOf("\\\""),EscapeSequence());
    }
    
    public Rule EscapeSequence() {
        return Sequence(
        		'\\',
        		AnyOf("\\\"")
        		);
    }

    
    // SYMBOLS and KEYWORDS
    // Results are stored in a Constant node
    
    /**
     * Pops a symbol stored in a Constant off the stack.
     * @return
     */
	protected Symbol popSymbol() {
    	Node<?> p=pop();
    	Symbol sym=p.getSymbol();
    	return sym;
    }
    
    public Rule Symbol() {
    	return FirstOf(QualifiedSymbol(),UnqualifiedSymbol());
    }    
     
    public Rule Keyword() {
    	return Sequence(
    			Sequence(':',Symbol()),
    			push(magic.ast.Constant.create(magic.data.Keyword.create(popSymbol()),getSourceInfo())));
    }    
    
    public Rule QualifiedSymbol() {
		return Sequence(
				UnqualifiedSymbol(),
				'/',
				UnqualifiedSymbol(),
				push(magic.ast.Lookup.create(Symbol.createWithNamespace(
						popSymbol().getName(),
						popSymbol().getName()),getSourceInfo()))
				);
	}

    public Rule UnqualifiedSymbol() {
		return Sequence(
				FirstOf(
						Sequence(InitialSymbolCharacter(),
								 ZeroOrMore(FollowingSymbolCharacter())),
				        Sequence(AnyOf(".+-"),
				        		 NonNumericSymbolCharacter(),
				        		 ZeroOrMore(FollowingSymbolCharacter())), 
						'/', // allowed on its own as a symbol
						'.' // dot special form
				        ),
				push(magic.ast.Lookup.create(Symbol.create(match()),getSourceInfo())));
	}

	public Rule InitialSymbolCharacter() {
		return FirstOf(Alphabet(),AnyOf(".*+!-_?$%&=<>"));
	}
	
	public Rule FollowingSymbolCharacter() {
		return FirstOf(AlphaNumeric(),AnyOf(".*+!-_?$%&=<>:#"));
	}
	
	public Rule NonNumericSymbolCharacter() {
		return FirstOf(Alphabet(),AnyOf(".*+!-_?$%&=<>:#"));
	}


	// CHARACTERS
    
    public Rule AlphaNumeric() {
    	return FirstOf(Alphabet(),Digit());
    }
    
    public Rule Alphabet() {
    	return FirstOf(CharRange('a','z'),CharRange('A','Z'));
    }
    
	public Rule Digit() {
        return CharRange('0', '9');
    }
	
	public Rule HexDigit() {
        return FirstOf(CharRange('0', '9'),CharRange('a','f'),CharRange('A','F'));
    }
	
    
    // NUMBERS
    
	public Rule NumberLiteral() {
		return FirstOf(Double(),Long());
	}
	
	public Rule Digits() {
        return OneOrMore(Digit());
    }
	
	public Rule SignedInteger() {
        return Sequence(
        		 Optional(AnyOf("+-")),
				 Digits());
    }
	
	public Rule Long() {
        return Sequence(
        		SignedInteger(),
        		push(magic.ast.Constant.create(Long.parseLong(match()),getSourceInfo())));
    }
	
	public Rule Double() {
        return Sequence(
        		Sequence(Optional(AnyOf("+-")),
        				 Optional(Digits()),
        				 '.',
        				 Digits(),
        				 Optional(ExponentPart())),
        		push(magic.ast.Constant.create(Double.parseDouble(match()),getSourceInfo()))
        		);
    }
	


	public Rule ExponentPart() {
        return Sequence(
        		AnyOf("eE"),
        		SignedInteger());
    }

	// MAIN PARSING FUNCTIONALITY
	
	protected SourceInfo getSourceInfo() {
		// IndexRange ir=matchRange();
		String source=getContext().getInputBuffer().toString();
		//int start=ir.start;
		// int end=ir.end;
		Position p=position();
		return SourceInfo.create(source,p.line,p.column);
	}
	
	private static Reader parser = Parboiled.createParser(Reader.class);
	
	private static <T> void checkErrors(ParsingResult<T> result) {
		if (result.hasErrors()) {
			java.util.List<ParseError> errors=result.parseErrors;
			StringBuilder sb=new StringBuilder();
			for (ParseError error: errors) {
				InputBuffer ib=error.getInputBuffer();
				int start=error.getStartIndex();
				int end=error.getEndIndex();
				sb.append("Parse error at "+ib.getPosition(error.getStartIndex())+": "+ib.extract(start, end)+" ERR: "+ error.getErrorMessage()+ "IB:"+ib.extractLine(1));
//				sb.append("Parse error at "+ib.getPosition(0)+": "+ib.extract(start, end)+" ERR: "+ error.getErrorMessage());
//				sb.append(result.parseTreeRoot);
			}
			throw new Error(sb.toString());
		}
		
	}
	
	/**
	 * Parses an expression and returns a form
	 * @param string
	 * @return
	 */
	public static Node<?> read(String source) {
		ParsingResult<Node<?>> result = new ReportingParseRunner<Node<?>>(parser.ExpressionInput()).run(source);
		checkErrors(result);
		return result.resultValue;
	}
	
	/**
	 * Parses an expression list and returns a collection form
	 * @param string
	 * @return
	 */
	public static magic.ast.ListForm readAll(String source) {
		ParsingResult<magic.ast.ListForm> result = new ReportingParseRunner<magic.ast.ListForm>(parser.Input()).run(source);
		checkErrors(result);
		return result.resultValue;
	}
	
	/**
	 * Parses a symbol
	 * @param string
	 * @return
	 */
	public static Symbol readSymbol(String source) {
		ParsingResult<Node<?>> result = new ReportingParseRunner<Node<?>>(parser.SymbolInput()).run(source);
		checkErrors(result);
		return result.resultValue.getSymbol();
	}
	
	/**
	 * Parses an expression and returns a form as an AST Node
	 * @param string
	 * @return
	 */
	public static Node<?> read(java.io.Reader source) throws IOException {
	    char[] arr = new char[8 * 1024];
	    StringBuilder buffer = new StringBuilder();
	    int numCharsRead;
	    while ((numCharsRead = source.read(arr, 0, arr.length)) != -1) {
	        buffer.append(arr, 0, numCharsRead);
	    }
	    return read(buffer.toString());
	}

	public static void main(String[] args) {
		Object result = read("[1 2 foo]");
		
		System.out.println(result);
	}
}
