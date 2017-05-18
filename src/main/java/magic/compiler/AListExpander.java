package magic.compiler;

import magic.ast.Form;
import magic.ast.Node;
import magic.lang.Context;

public abstract class AListExpander extends Expander {

	@Override
	public Node<?> expand(Context c, Node<?> form, Expander ex) {
		if (form instanceof Form) return expand(c,(Form<?>)form,ex);
		throw new ExpansionException("Expansion failed, expecting a list but got: "+form,form);
	}
	
	public Node<?> expand(Context c, Form<?> form, Expander ex) {
		throw new ExpansionException("Expansion failed for expander of type "+this.getClass()+": "+form,form);
		
	}

}
