package magic.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

import magic.data.ListFactory;

@BuildParseTree
public class Parser extends BaseParser<Object> {

	public static Object parse(Reader source) throws IOException {
	    char[] arr = new char[8 * 1024];
	    StringBuilder buffer = new StringBuilder();
	    int numCharsRead;
	    while ((numCharsRead = source.read(arr, 0, arr.length)) != -1) {
	        buffer.append(arr, 0, numCharsRead);
	    }
	    return parse(buffer.toString());
	}
	
	public Rule Expression() {
		return FirstOf(
				Vector(),
				Constant()
				);
	}
	
	public Rule Vector() {
		return Sequence(
				'[',
				ExpressionList(),
				']');
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
				push(ListFactory.createFromList(expVar.get()))
				);
	}
	
	public Rule Constant() {
		return FirstOf(NumberLiteral(),StringLiteral());
	}
	
	public Rule NumberLiteral() {
		return FirstOf(Double(),Long());
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
	
    public Rule WhiteSpace() {
        return OneOrMore(WhiteSpaceCharacter());
    }
    
    public Rule WhiteSpaceCharacter() {
        return AnyOf(" \t\f,\r\n");
    }

	public Rule Long() {
        return Sequence(
        		Sequence(Optional('-'),
        				 OneOrMore(Digit())),
        		push(Long.parseLong(match())));
    }
	
	public Rule Double() {
        return Sequence(
        		Sequence(Optional('-'),
        				 OneOrMore(Digit()),
        				 '.',
        				 OneOrMore(Digit())),
        		push(Double.parseDouble(match())));
    }
    

	public Rule Digit() {
        return CharRange('0', '9');
    }
	
	private static Parser parser = Parboiled.createParser(Parser.class);
	private static final RecoveringParseRunner<Object> expressionParseRunner=new RecoveringParseRunner<>(parser.Expression());

	
	/**
	 * Parses an expression and results a form
	 * @param string
	 * @return
	 */
	public static Object parse(String source) {
		ParsingResult<Object> result = expressionParseRunner.run(source);
		return result.resultValue;
	}
	


	public static void main(String[] args) {
		Object result = parse("[1 2 3]");
		
		System.out.println(result);
	}



}
