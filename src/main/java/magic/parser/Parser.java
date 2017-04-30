package magic.parser;

import java.io.IOException;
import java.io.Reader;
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
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

import magic.data.Vectors;
import magic.data.IPersistentCollection;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Sets;
import magic.data.Symbol;

@BuildParseTree
public class Parser extends BaseParser<Object> {

	// OVERALL PARSING INPUT RULES
	
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
				Symbol(),
				Constant()
				);
	}
	
	Action<Object> AddAction(Var<ArrayList<Object>> expVar) {
		return new Action<Object>() {
			@Override
			public boolean run(Context<Object> context) {
				Object o=pop();
				// System.out.println(o);
				expVar.get().add(o);
				return true;
			}
		};
	}
	
	public Rule ExpressionList() {
		Var<ArrayList<Object>> expVar=new Var<>(new ArrayList<>());
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
				push(Vectors.createFromList(expVar.get()))
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
	
	public Rule List() {
		return Sequence(
				'(',
				ExpressionList(),
				')',
				push(Lists.create((IPersistentCollection<?>) pop())));
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
		return FirstOf(NumberLiteral(),StringLiteral());
	}
	
	public Rule StringLiteral() {
		StringVar sb=new StringVar("");

		return Sequence(
				'"',
				ZeroOrMore(Sequence(StringCharacter(),sb.append(matchOrDefault("0")))),
				push(sb.get().toString()),
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

    
    // SYMBOLS
    
    public Rule Symbol() {
    	return FirstOf(QualifiedSymbol(),UnqualifiedSymbol());
    }    
    
    public Rule QualifiedSymbol() {
		return Sequence(
				UnqualifiedSymbol(),
				'/',
				UnqualifiedSymbol(),
				push(Symbol.createWithNamespace(
						((Symbol)pop()).getName(),
						((Symbol)pop()).getName())) );
	}

    public Rule UnqualifiedSymbol() {
		return Sequence(
				FirstOf(
						'/', // allowed on its own as a symbol
						Sequence(InitialSymbolCharacter(),
								 ZeroOrMore(FollowingSymbolCharacter())),
				        Sequence(AnyOf(".+-"),
				        		 NonNumericSymbolCharacter(),
				        		 ZeroOrMore(FollowingSymbolCharacter())) ),
				push(Symbol.create(match())));
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
        		push(Long.parseLong(match())));
    }
	
	public Rule Double() {
        return Sequence(
        		Sequence(Optional(AnyOf("+-")),
        				 Optional(Digits()),
        				 '.',
        				 Digits(),
        				 Optional(ExponentPart())),
        		push(Double.parseDouble(match())));
    }
	
	public Rule ExponentPart() {
        return Sequence(
        		AnyOf("eE"),
        		SignedInteger());
    }

	// MAIN PARSING FUNCTIONALITY
	
	private static Parser parser = Parboiled.createParser(Parser.class);
	private static final ReportingParseRunner<Object> expressionParseRunner=new ReportingParseRunner<>(parser.ExpressionInput());
	
	/**
	 * Parses an expression and returns a form
	 * @param string
	 * @return
	 */
	public static Object parse(String source) {
		ParsingResult<Object> result = expressionParseRunner.run(source);
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
		return result.resultValue;
	}
	
	/**
	 * Parses an expression and returns a form
	 * @param string
	 * @return
	 */
	public static Object parse(Reader source) throws IOException {
	    char[] arr = new char[8 * 1024];
	    StringBuilder buffer = new StringBuilder();
	    int numCharsRead;
	    while ((numCharsRead = source.read(arr, 0, arr.length)) != -1) {
	        buffer.append(arr, 0, numCharsRead);
	    }
	    return parse(buffer.toString());
	}

	public static void main(String[] args) {
		Object result = parse("[1 2 3] [2]");
		
		System.out.println(result);
	}
}
