package magic;

import java.io.FileNotFoundException;

import magic.lang.Context;

/**
 * Main class for Magic execution
 * 
 * @author Mike
 */
public class Main {

	static final Context MAIN_CONTEXT=Core.INITIAL_CONTEXT;
	
	public static void main(String... args) throws FileNotFoundException {
		int alen=args.length;
		
		if (alen==1) {
			String a0=args[0];
			if (a0.equals("repl")) {
				Repl.main(args);
				System.exit(0);
			}
		}
		
		if (args.length==0||(args[0].equals("help"))) {
			System.out.println("Magic! 0.0.1");
			System.out.println("usage: java -jar magic.jar <args>");
			System.out.println("where <args> are one of:");
			System.out.println("   help          => Display this help message");
			System.out.println("   <filename>    => Execute a magic .mag script with the gievn filename");
			System.out.println("   repl          => Launch a standard Magic REPL");
			System.exit(0);
		}
		
		String s=RT.getResourceAsString(args[0]);
		Core.eval(MAIN_CONTEXT,s);
	}

}
