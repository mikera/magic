package magic.compiler;

import magic.Type;
import magic.Types;
import magic.ast.Node;
import magic.data.APersistentSequence;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn1;
import magic.fn.IFn3;
import magic.lang.Context;

/**
 * An expander expands code given a context, a form and a continuation expander 
 * 
 * Expander must return a Node
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class AExpander extends AFn<Node<?>> implements IFn3<Node<?>> {

	@Override
	public Node<?> apply(Object o1, Object o2, Object o3) {
		return expand((Context)o1,(Node<?>)o2,(AExpander)o3);
	}

	@Override
	public final Node<?> applyToArray(Object... a) {
		if (a.length!=3) throw new ArityException("Expander requires 3 arguments (context, form, next-expander)");
		return expand((Context)a[0],(Node<?>) a[1],(AExpander)a[2]);
	}
	
	/**
	 * Expands a Node
	 * 
	 * @param c The context in which to expand
	 * @param form The node to be expanded.
	 * @param ex The continuation expander. Should normally be used to expand resulting nodes from this expander, unless special sematrics are required.
	 * @return
	 */	
	public abstract Node<?> expand(Context c, Node<?> form, AExpander ex);

	/**
	 * Expands a sequence of nodes using this expander
	 * @param c
	 * @param forms
	 * @param ex
	 * @return
	 */
	public APersistentSequence<Node<?>> expandAll(Context c, APersistentSequence<Node<?>> forms,AExpander ex) {
		return forms.map(new IFn1<Node<?>,Node<?>>(){
			@Override
			public Node<?> apply(Object a) {
				return expand(c,(Node<?>) a,ex);
			}
		});
	}

	@Override
	public Type getType() {
		return Types.EXPANDER;
	}

}
