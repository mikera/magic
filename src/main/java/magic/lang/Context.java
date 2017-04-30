package magic.lang;

import magic.expression.Expression;

/**
 * Class representing an Execution context.
 * 
 * Manages a Map of Symbol->Slot
 * @author Mike
 *
 */
public class Context {
	public static final Context EMPTY=new Context();

	public Context() {
		
	}
	
	public <T> T evaluate(Expression<T> e) {
		return e.compute(this);
	}

}
