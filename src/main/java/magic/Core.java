package magic;

import java.io.FileNotFoundException;

import magic.ast.Constant;
import magic.compiler.EvalResult;
import magic.compiler.Expanders;
import magic.fn.Functions;
import magic.lang.Context;

public class Core {

	public static final Context BOOTSTRAP_CONTEXT = createBootstrapContext();
	public static final Context INITIAL_CONTEXT = createInitialContext();
	/**
	 * Sets up the initial Magic context for language bootstrap
	 * This is what is required to load magic.core
	 * @return
	 */
	private static Context createBootstrapContext() {
		Context c=Context.EMPTY;
		try {
			c=c.define(Symbols.DEF, Constant.create(Expanders.DEF));
			c=c.define(Symbols.DEFN, Constant.create(Expanders.DEFN));
			c=c.define(Symbols.FN, Constant.create(Expanders.FN));
			c=c.define(Symbols.EXPANDER, Constant.create(Expanders.EXPANDER));
			c=c.define(Symbols.LET, Constant.create(Expanders.LET));
			c=c.define(Symbols.DO, Constant.create(Expanders.DO));
			c=c.define(Symbols.DOT, Constant.create(Expanders.DOT));
			c=c.define(Symbols.IF, Constant.create(Expanders.IF));
			c=c.define(Symbols.DEFMACRO, Constant.create(Expanders.DEFMACRO));
			c=c.define(Symbols.MACRO, Constant.create(Expanders.MACRO));
			c=c.define(Symbols.QUOTE, Constant.create(Expanders.QUOTE));
			c=c.define(Symbols.UNQUOTE, Constant.create(Expanders.UNQUOTE));
			c=c.define(Symbols.SYNTAX_QUOTE, Constant.create(Expanders.QUOTE));
			c=c.define(Symbols.RETURN, Constant.create(Expanders.RETURN));
					
			c=c.define(Symbols.VECTOR, Constant.create(Expanders.VECTOR)); 
			c=c.define(Symbols.LIST, Constant.create(Expanders.LIST)); 
			c=c.define(Symbols.SET, Constant.create(Expanders.SET)); 
			c=c.define(Symbols.HASHMAP, Constant.create(Expanders.HASHMAP)); 
					
			c=c.define(Symbols.INSTANCE_Q, Constant.create(Expanders.INSTANCEOF)); 
			c=c.define(Symbols.CAST, Constant.create(Expanders.CAST)); 
	
			c=c.define(Symbols.ANY, Constant.create(Types.ANY)); 
			c=c.define(Symbols.NONE, Constant.create(Types.NONE)); 
			c=c.define(Symbols.NULL, Constant.create(Types.NULL)); 
			
			c=c.define(Symbols.EQUALS, Constant.create(Functions.EQUALS)); 
			c=c.define(Symbols.PRINTLN, Constant.create(Functions.PRINTLN)); 
			c=c.define(Symbols.PLUS, Constant.create(Functions.LONGADD)); 

	
			
			c=c.define(Symbols._NS_, Constant.create(null)); 
	
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
		return c;				
	}
	/**
	 * Loads magic.core to create the initial user context
	 * @return
	 * @throws FileNotFoundException 
	 */
	private static Context createInitialContext() {
		Context c=BOOTSTRAP_CONTEXT;
		EvalResult<?> r;
		try {
			r=(EvalResult<?>) magic.compiler.Compiler.compile(c, RT.getResourceAsString("magic/core.mag"));
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			throw new magic.Error("Failed to initialise Magic environment",t);
		}
		return r.getContext();
	}
	/**
	 * Compiles and evaluates code in the initial context
	 * @param code
	 * @return an EvalResult containing the resulting value and a (possibly) updated context
	 */
	public static EvalResult<?> compile(String code) {
		return compile(INITIAL_CONTEXT,code);
	}
	/**
	 * Compiles and evaluates code in the initial context
	 * @param code
	 * @return an EvalResult containing the resulting value and a (possibly) updated context
	 */
	public static EvalResult<?> compile(Context c,String code) {
		return magic.compiler.Compiler.compile(c, code);
	}

}
