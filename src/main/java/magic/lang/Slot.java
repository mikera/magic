package magic.lang;

/**
 * Represents a "slot" in a magic Context.
 * 
 * A slot features:
 * - An Expression
 * - A lazily computed value
 * 
 * @author Mike
 *
 */
public class Slot {
	public final Expression expression;
	
	private Object value=null;
	private volatile boolean computed=false;
	
	public Slot(Expression e) {
		this.expression=e;
	}
	
	public Object getValue(Context c) {
		if (computed==false) {
			synchronized (this) {
				if (computed==false) {
					value=expression.compute(c);
					computed=true;
					return value;
				}
			}
		}
		return value;
	}
}
