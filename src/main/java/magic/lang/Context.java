package magic.lang;

/**
 * Class representing an Execution context.
 * 
 * Manages a Map of Symbol->Slot
 * @author Mike
 *
 */
public class Context {

	public Context() {
		
	}
	
	public Object evaluate(Expression e) {
		return e.compute(this);
	}

}
