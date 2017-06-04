package magic.lang;

import magic.ast.Node;
import magic.compiler.AExpander;
import magic.data.APersistentSet;
import magic.data.Symbol;

/**
 * Represents a "slot" in a magic Context.
 * 
 * A slot features:
 * - An expression stored as a Node
 * - A lazily computed value
 * 
 * @author Mike
 *
 * @param T the Java type of the expression
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
					return tryCompute(c);
				}
			}
		}
		return value;
	}
	
	private T tryCompute(Context c) {
//		APersistentSet<Symbol> deps=expression.getDependencies();
//		if (!deps.isEmpty()) {
//			// check slots exist
//			for (Symbol s:deps) {
//				if (c.getSlot(s)==null) throw new UnresolvedException(s);
//			}
//		}
		value=expression.compute(c);
		computed=true;
		return value;
	}
	
	public Node<T> getNode() {
		return expression;
	}


	public static <T> Slot<T> create(Node<T> exp) {
		return new Slot<T>(exp);
	}

	public boolean isExpander(Context c) {
		return getValue(c) instanceof AExpander;
	}

	public APersistentSet<Symbol> getDependencies() {
		return expression.getDependencies();
	}

	/**
	 * Invalidates the slot, returning a new slot with no cached values
	 * @return
	 */
	public Slot<T> invalidate() {
		return create(expression);
	}
}
