package magic.parser;

import java.io.IOException;
import java.io.Reader;
import magic.expression.Constant;
import magic.expression.Expression;

public class Parser {
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
}
