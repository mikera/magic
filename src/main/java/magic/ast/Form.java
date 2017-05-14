package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Represents a raw, unexpanded form
 * 
 * Dependency initially exists on the first symbol only: this must be resolved in order to expansion to continue.
 * 
 * @author Mike
 */
public class Form<T> extends Node<T> {

	private Object form;
	
	private Form(Object form,Symbol dep,SourceInfo source) {
		super((dep==null)?Sets.emptySet():dep.symbolSet(),source);
		this.form=form;
	}
	
	public static <T> Form<T> create(Object form,Symbol dep,SourceInfo source) {
		return new Form<>(form,dep,source);
	}
	
	public static <T> Form<T> create(Object form,Symbol dep) {
		return create(form,dep,null);
	}
	
	public Object getForm() {
		return form;
	}
	
	@Override
	public APersistentSet<Symbol> getDependencies() {
		throw new UnsupportedOperationException("Trying to get dependencies for unexpanded form: "+form);
	}

	
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		throw new UnsupportedOperationException("Trying to evaluate unexpanded form: "+this);
	}
	
	@Override
	public String toString() {
		return RT.toString(form);
	}
 	
	@Override
	public Node<T> optimise() {
		return this;
	}

	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return this;
	}
}
