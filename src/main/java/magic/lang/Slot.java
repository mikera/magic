package magic.lang;

import magic.ast.Node;
import magic.compiler.Expander;
import magic.data.APersistentSet;
import magic.data.Symbol;

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
					return tryCompute(c);
				}
			}
		}
		return value;
	}
	
	private T tryCompute(Context c) {
		APersistentSet<Symbol> deps=expression.getDependencies();
		if (!deps.isEmpty()) {
			// check slots exist
			for (Symbol s:deps) {
				if (c.getSlot(s)==null) throw new UnresolvedException(s);
			}
		}
		value=expression.compute(c);
		computed=true;
		return value;
	}
	
	public Node<T> getExpression() {
		return expression;
	}


	public static <T> Slot<T> create(Node<T> exp) {
		return new Slot<T>(exp);
	}

	public boolean isExpander(Context c) {
		return getValue(c) instanceof Expander;
	}
}
