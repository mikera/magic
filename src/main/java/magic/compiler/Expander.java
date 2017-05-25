package magic.compiler;

import magic.ast.Node;
import magic.data.APersistentList;
import magic.data.APersistentSequence;
import magic.fn.AFn;
import magic.fn.AFn1;
import magic.fn.ArityException;
import magic.fn.IFn1;
import magic.fn.IFn3;
import magic.lang.Context;

/**
 * An expander expands code given a context, a form and a continuation expander 
 * @author Mike
 *
 */
public abstract class Expander extends AFn<Object> implements IFn3<Object> {
	@Override
	public Object apply(Object o1, Object o2, Object o3) {
		return expand((Context)o1,(Node<?>)o2,(Expander)o3);
	}

	@Override
	public final Node<?> applyToArray(Object... a) {
		if (a.length!=3) throw new ArityException("Expander requires 3 arguments (context, form, next-expander)");
		return expand((Context)a[0],(Node<?>) a[1],(Expander)a[2]);
	}
	
	public abstract Node<?> expand(Context c, Node<?> form, Expander ex);

	public APersistentSequence<Node<?>> expandAll(Context c, APersistentSequence<Node<?>> forms,Expander ex) {
		return forms.map(new IFn1<Node<?>,Node<?>>(){
			@Override
			public Node<?> apply(Object a) {
				return expand(c,(Node<?>) a,ex);
			}
		});
	}


}
