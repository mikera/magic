package magic.ast;

import magic.data.IPersistentSet;
import magic.data.PersistentHashMap;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Represents a raw form
 * 
 * @author Mike
 */
public class Form extends Node<Object> {

	private Object form;
	
	private Form(Object form) {
		super(Sets.emptySet());
		this.form=form;
	}
	
	public Form create(Object form) {
		return new Form(form);
	}
	
	public Object getForm() {
		return form;
	}
	
	@Override
	public IPersistentSet<Symbol> getDependencies() {
		throw new UnsupportedOperationException("Trying to get dependencies unexpanded form");
	}

	
	@Override
	public Object compute(Context c, PersistentHashMap<Symbol, ?> bindings) {
		throw new UnsupportedOperationException("Trying to evaluate unexpanded form");
	}

	
}
