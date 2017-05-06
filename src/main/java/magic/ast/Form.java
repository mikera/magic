package magic.ast;

import magic.data.PersistentHashMap;
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
		this.form=form;
	}
	
	public Form create(Object form) {
		return new Form(form);
	}
	
	public Object getForm() {
		return form;
	}
	
	@Override
	public Object compute(Context c, PersistentHashMap<Symbol, ?> bindings) {
		throw new UnsupportedOperationException("Trying to evaluate unexpanded form");
	}

	
}
