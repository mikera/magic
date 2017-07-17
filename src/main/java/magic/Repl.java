package magic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import magic.compiler.EvalResult;
import magic.data.Symbol;
import magic.lang.Context;

public class Repl {

	private static void runRepl() {
		System.out.println();
		System.out.println("  *** Welcome to Magic ***");
		System.out.println();
		System.out.println("  Version: "+Repl.class.getPackage().getImplementationVersion());
		System.out.println("  Type 'quit' to exit REPL");
		System.out.println();

		Context c=Main.MAIN_CONTEXT;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String namespace=c.getCurrentNamespace();
			try {
				System.out.print(namespace+"=>");
				String line = reader.readLine();
				
				String tline=line.trim();
				if (tline.equals("quit")) break;
				
				EvalResult<?> r=Core.eval(c,line);
				c=r.getContext();
				Object result=r.getValue();
				System.out.println(RT.print(result));
			} catch (Throwable e) {
				c=c.put(Symbol.create(namespace, "*e"),e);
				System.out.println(e);
				// System.err.flush();
				try {
					// try to avoid intermixing err output with next prompt
					Thread.sleep(1);
				} catch (InterruptedException e1) {
					// nothing
				} 
			}
		}
	}

	public static void main(String... args) {
		runRepl();
		
	}

}
