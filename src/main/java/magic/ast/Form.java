package magic.ast;

import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.IPersistentSet;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Represents a raw, unexpanded form
 * Dependency generally exists on the first symbol only
 * 
 * @author Mike
 */
public class Form<T> extends Node<T> {

	private Object form;
	
	private Form(Object form,Symbol dep) {
		super((dep==null)?Sets.emptySet():dep.symbolSet());
		this.form=form;
	}
	
	public static <T> Form<T> create(Object form,Symbol dep) {
		return new Form<>(form,dep);
	}
	
	public Object getForm() {
		return form;
	}
	
	@Override
	public IPersistentSet<Symbol> getDependencies() {
		throw new UnsupportedOperationException("Trying to get dependencies for unexpanded form: "+form);
	}

	
	@Override
	public Result<T> eval(Context c, APersistentMap<Symbol, ?> bindings) {
		throw new UnsupportedOperationException("Trying to evaluate unexpanded form: "+this);
	}
	
	@Override
	public String toString() {
		return form.toString();
	}
 	
}
