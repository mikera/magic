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
import magic.expression.Constant;
import magic.expression.Expression;
import magic.expression.LongConstant;

@BuildParseTree
public class MagicParser extends BaseParser<Expression<?>> {
	public static Expression<?> parse(String source) {
		return Constant.create("Parsed test constant");
	}
	
	public static Expression<?> parse(Reader source) throws IOException {
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
	
	Action<?> AddAction(Var<ArrayList<Expression<?>>> expVar) {
		return new Action<Object>() {
			@Override
			public boolean run(Context<Object> context) {
				Expression<?> o=pop();
				System.out.println(o);
				expVar.get().add(o);
				return true;
			}
		};
	}
	
	public Rule ExpressionList() {
		Var<ArrayList<Expression<?>>> expVar=new Var<>(new ArrayList<>());
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
				push(magic.expression.Vector.create(ListFactory.createFromList(expVar.get())))
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
				push(Constant.create(sb.get().toString())),
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
        		push(LongConstant.create(Long.parseLong(match()))));
    }
	
	public Rule Double() {
        return Sequence(
        		Sequence(Optional('-'),
        				 OneOrMore(Digit()),
        				 '.',
        				 OneOrMore(Digit())),
        		push(Constant.create(Double.parseDouble(match()))));
    }
    

	public Rule Digit() {
        return CharRange('0', '9');
    }
	
	private static MagicParser parser = Parboiled.createParser(MagicParser.class);
	private static final RecoveringParseRunner<Expression<?>> expressionParseRunner=new RecoveringParseRunner<>(parser.Expression());

	public static Expression<?> parseExpression(String string) {
		ParsingResult<Expression<?>> result = expressionParseRunner.run(string);
		return result.resultValue;
	}

	public static void main(String[] args) {
		Expression<?> result = parseExpression("[1 2 3]");
		
		System.out.println(result);
	}



}
