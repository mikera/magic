package magic.compiler;

import magic.Type;
import magic.Types;
import magic.ast.ListForm;
import magic.ast.Node;
import magic.lang.Context;

public abstract class AListExpander extends AExpander {

	@Override
	public final Node<?> expand(Context c, Node<?> form, AExpander ex) {
		if (form instanceof ListForm) return expand(c,(ListForm)form,ex);
		throw new ExpansionException("Expansion failed, expecting a List but got type: "+form.getClass().getName(),form);
	}
	
	/**
	 * Expands a List form
	 * 
	 * @param c The context in which to expand
	 * @param form The List node that represents the form to be expanded.
	 * @param ex The continuation expander. Should normally be used to expand resulting nodes from this expander, unless special sematrics are required.
	 * @return
	 */
	public abstract Node<?> expand(Context c, ListForm form, AExpander ex);
	
	@Override
	public Type getType() {
		return Types.EXPANDER;
	}

}
