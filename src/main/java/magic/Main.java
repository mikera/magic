package magic;

import java.io.FileNotFoundException;

import magic.compiler.EvalResult;
import magic.lang.Context;

/**
 * Main class for Magic execution
 * 
 * @author Mike
 */
public class Main {

	private static final Context MAIN_CONTEXT=RT.INITIAL_CONTEXT;
	
	public static void main(String... args) throws FileNotFoundException {
		if (args.length>0) {
			String s=RT.getResourceAsString(args[0]);
			evaluate(MAIN_CONTEXT,s);
		} else {
			System.out.println("Magic! 0.0.1");
		}
	}

	private static EvalResult<?> evaluate(Context c, String s) {
		EvalResult<?> r=magic.compiler.Compiler.compile(c,s);
		return r;	
	}
}
