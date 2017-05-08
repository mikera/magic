package magic.compiler;

import magic.data.APersistentList;
import magic.lang.Context;

public abstract class AListExpander extends Expander {

	@SuppressWarnings("unchecked")
	@Override
	public Object expand(Context c, Object form, Expander ex) {
		if (form instanceof APersistentList) return expand(c,(APersistentList<Object>)form,ex);
		throw new ExpansionException("Expansion failed, expecting a list",form);
	}
	
	public abstract Object expand(Context c, APersistentList<Object> form,Expander ex);

}
