package magic.compiler;

import magic.ast.List;
import magic.ast.Node;
import magic.lang.Context;

public abstract class AListExpander extends Expander {

	@Override
	public final Node<?> expand(Context c, Node<?> form, Expander ex) {
		if (form instanceof List) return expand(c,(List)form,ex);
		throw new ExpansionException("Expansion failed, expecting a list but got: "+form,form);
	}
	
	public abstract Node<?> expand(Context c, List form, Expander ex);

}
