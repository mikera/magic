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

	private static final Context MAIN_CONTEXT=Core.INITIAL_CONTEXT;
	
	public static void main(String... args) throws FileNotFoundException {
		if (args.length==0||(args[0].equals("help"))) {
			System.out.println("Magic! 0.0.1");
			System.out.println("usage: java -jat magic.jar <args>");
			System.out.println("where <args> are one of:");
			System.out.println("   help          => Display this help message");
			System.out.println("   <filename>    => Execute a magic .mag script with the gievn filename");
			System.out.println("   repl          => Launch a standard Magic REPL (TODO)");
			System.exit(0);
		}
		String s=RT.getResourceAsString(args[0]);
		evaluate(MAIN_CONTEXT,s);
	}

	private static EvalResult<?> evaluate(Context c, String s) {
		EvalResult<?> r=magic.compiler.Compiler.eval(c,s);
		return r;	
	}
}
