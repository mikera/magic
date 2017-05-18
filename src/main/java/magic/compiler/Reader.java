package magic.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import magic.ast.Form;
import magic.ast.Node;
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Symbols;

/**
 * Parboiled Parser implementation which reads Magic source code and produces a tree of parsed objects.
 * 
 * 
 * @author Mike
 *
 */
@BuildParseTree
public class Reader extends BaseParser<Object> {

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
	
    public Rule WhiteSpace() {
        return OneOrMore(WhiteSpaceCharacter());
    }
    
    public Rule WhiteSpaceCharacter() {
        return AnyOf(" \t\f,\r\n");
    }
	
	// EXPRESSIONS
	
	public Rule Expression() {
		return FirstOf(
				DataStructure(),
				Constant(),
				Keyword(),
				Symbol(),
				QuotedExpression()
				);
	}

	Action<Object> AddAction(Var<ArrayList<Node<?>>> expVar) {
		return new Action<Object>() {
			@Override
			public boolean run(Context<Object> context) {
				Object o=pop();
				// System.out.println(o);
				expVar.get().add((Node<?>) o);
				return true;
			}
		};
	}
	
	public Rule ExpressionList() {
		Var<ArrayList<Node<?>>> expVar=new Var<>(new ArrayList<>());
		return Sequence(
				Optional(WhiteSpace()),
				FirstOf(Sequence(
						 Expression(),
						 AddAction(expVar),
						 ZeroOrMore(Sequence(WhiteSpace(),
								 			 Expression(),
								 		  	 AddAction(expVar))),
						 Optional(WhiteSpace())),
						 EMPTY
						),
				push(magic.ast.Vector.create(expVar.get(),getSourceInfo()))
				);
	}
	
	// QUOTING and UNQUOTING
	
	public Rule QuotedExpression() {
		return FirstOf(
				Quote(),
				SyntaxQuote(),
				Unquote(), 
				UnquoteSplice()
				);
	}
	
	public Rule Quote() {
		return Sequence(
				'\'',
				Expression(),
				push(PersistentList.of(Symbols.QUOTE,pop()))
				);
	}
	
	public Rule SyntaxQuote() {
		return Sequence(
				'`',
				Expression(),
				push(PersistentList.of(Symbols.SYNTAX_QUOTE,pop()))
				);
	}
	
	public Rule Unquote() {
		return Sequence(
				'~',
				Expression(),
				push(PersistentList.of(Symbols.UNQUOTE,pop()))
				);
	}
	
	public Rule UnquoteSplice() {
		return Sequence(
				"~@",
				Expression(),
				push(PersistentList.of(Symbols.UNQUOTE_SPLICING,pop()))
				);
	}
	
	// DATA TYPE LITERALS
	
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
				']');
	}
	
	@SuppressWarnings("unchecked")
	public Rule List() {
		return Sequence(
				'(',
				ExpressionList(),
				')',
				push(Form.create((magic.ast.Vector<Object>) pop(),getSourceInfo())));
	}
	
	public Rule Set() {
		return Sequence(
				"#{",
				ExpressionList(),
				'}',
				push(Sets.createFrom((List<?>)pop())));
	}
	
	public Rule Map() {
		return Sequence(
				"{",
				ExpressionList(),
				'}',
				push(Maps.createFromFlattenedPairs((List<?>)pop())));
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
				push(null));
	}
	
	public Rule CharLiteral() {
		return Sequence(
				'\\',
				FirstOf(Sequence("newline",push('\n')),
						Sequence("space",push(' ')),
						Sequence("tab",push('\t')),
						Sequence("formfeed",push('\f')),
						Sequence("backspace",push('\b')),
						Sequence("return",push('\r')),
						Sequence("u", 
								NTimes(4,HexDigit()),
								push(push(magic.ast.Constant.create((char) Long.parseLong(match(), 16),getSourceInfo()))))
						));
	}
	
	public Rule BooleanLiteral() {
		return FirstOf(
				Sequence("true",push(Boolean.TRUE)),
				Sequence("false",push(Boolean.FALSE)));
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
    
    public Rule Symbol() {
    	return FirstOf(QualifiedSymbol(),UnqualifiedSymbol());
    }    
     
    public Rule Keyword() {
    	return Sequence(
    			Sequence(':',Symbol()),
    			push(magic.ast.Constant.create(magic.data.Keyword.create((Symbol)pop()),getSourceInfo())));
    }    
    
    public Rule QualifiedSymbol() {
		return Sequence(
				UnqualifiedSymbol(),
				'/',
				UnqualifiedSymbol(),
				push(magic.ast.Constant.create(Symbol.createWithNamespace(
						((Symbol)pop()).getName(),
						((Symbol)pop()).getName()),getSourceInfo())));
	}

    public Rule UnqualifiedSymbol() {
		return Sequence(
				FirstOf(
						'/', // allowed on its own as a symbol
						'.', // dot special form
						Sequence(InitialSymbolCharacter(),
								 ZeroOrMore(FollowingSymbolCharacter())),
				        Sequence(AnyOf(".+-"),
				        		 NonNumericSymbolCharacter(),
				        		 ZeroOrMore(FollowingSymbolCharacter())) ),
				push(magic.ast.Constant.create(Symbol.create(match()),getSourceInfo())));
	}

	public Rule InitialSymbolCharacter() {
		return FirstOf(Alphabet(),AnyOf("*!_?$%&=<>"));
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
	private static final ReportingParseRunner<magic.ast.Vector<?>> inputParseRunner=new ReportingParseRunner<>(parser.Input());
	private static final ReportingParseRunner<Node<?>> expressionParseRunner=new ReportingParseRunner<>(parser.ExpressionInput());
	private static final ReportingParseRunner<magic.ast.Constant<Symbol>> symbolParseRunner=new ReportingParseRunner<>(parser.Symbol());
	
	private static <T> void checkErrors(ParsingResult<T> result) {
		if (result.hasErrors()) {
			List<ParseError> errors=result.parseErrors;
			StringBuilder sb=new StringBuilder();
			for (ParseError error: errors) {
				InputBuffer ib=error.getInputBuffer();
				sb.append("Parse error at "+ib.getPosition(error.getStartIndex())+": "+ error.getErrorMessage());
				sb.append("\n");
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
		ParsingResult<Node<?>> result = expressionParseRunner.run(source);
		checkErrors(result);
		return result.resultValue;
	}
	
	/**
	 * Parses an expression list and returns a collection form
	 * @param string
	 * @return
	 */
	public static magic.ast.Vector<?> readAll(String source) {
		ParsingResult<magic.ast.Vector<?>> result = inputParseRunner.run(source);
		checkErrors(result);
		return result.resultValue;
	}
	
	/**
	 * Parses a symbol
	 * @param string
	 * @return
	 */
	public static Symbol readSymbol(String source) {
		ParsingResult<magic.ast.Constant<magic.data.Symbol>> result = symbolParseRunner.run(source);
		checkErrors(result);
		return result.resultValue.getValue();
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
		Object result = read("[1 2 3]");
		
		System.out.println(result);
	}
}
