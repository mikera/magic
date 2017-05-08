package magic.compiler;

import magic.data.IPersistentList;
import magic.lang.Context;

public abstract class ListExpander extends Expander {

	@SuppressWarnings("unchecked")
	@Override
	public Object expand(Context c, Object form, Expander ex) {
		if (form instanceof IPersistentList) return expand(c,(IPersistentList<Object>)form,ex);
		throw new ExpansionFailedException("Expansion failed, expecting a list",form);
	}
	
	public abstract Object expand(Context c, IPersistentList<Object> form,Expander ex);

}