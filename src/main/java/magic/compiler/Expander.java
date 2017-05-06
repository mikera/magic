package magic.compiler;

import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn3;
import magic.lang.Context;

/**
 * An expander expands a code given a context, a form and a continuation expander 
 * @author Mike
 *
 */
public abstract class Expander extends AFn<Object> implements IFn3<Object> {
	@Override
	public Object apply(Object o1, Object o2, Object o3) {
		return expand((Context)o1,o2,(Expander)o3);
	}


	@Override
	public final Object applyToArray(Object... a) {
		if (a.length!=3) throw new ArityException("Expander requires 3 arguments (context, form, next-expander)");
		return expand((Context)a[0],a[1],(Expander)a[2]);
	}
	
	public abstract Object expand(Context c, Object form, Expander ex);


}
