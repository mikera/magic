package magic.lang;

import magic.ast.Node;

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
public class Slot<T> {
	public final Node<T> expression;
	
	private T value=null;
	private volatile boolean computed=false;
	
	public Slot(Node<T> e) {
		this.expression=e;
	}
	
	public T getValue(Context c) {
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

	public static <T> Slot<T> create(Node<T> exp) {
		return new Slot<T>(exp);
	}
}
