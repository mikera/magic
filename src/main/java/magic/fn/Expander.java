package magic.fn;

import magic.ast.Node;
import magic.lang.Context;

/**
 * An expander expands a code given a context, a form and a continuation expander 
 * @author Mike
 *
 */
public abstract class Expander<T> extends AFn<Node<T>> implements IFn3<Node<T>> {
	@Override
	public Node<T> apply(Object o1, Object o2, Object o3) {
		return expand((Context)o1,o2,(Expander<?>)o3);
	}


	@Override
	public final Node<T> applyToArray(Object... a) {
		if (a.length!=3) throw new ArityException("Expander requires 3 arguments (context, form, next-expander)");
		return expand((Context)a[0],a[1],(Expander<?>)a[2]);
	}
	
	public abstract Node<T> expand(Context c, Object form, Object ex);


}
