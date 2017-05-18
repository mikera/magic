package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.lang.Context;

/**
 * Represents a raw, unexpanded list form.
 * 
 * Form may contain:
 * - Literal objects
 * - Data structures
 * - Other nodes
 * 
 * Dependency initially exists on the first symbol only: this must be resolved in order for expansion to continue.
 * 
 * @author Mike
 */
public class Form<T> extends Node<T> {

	@SuppressWarnings("unchecked")
	public static final Form<Object> EMPTY = Form.create((APersistentList<Object>)PersistentList.EMPTY);
	
	private APersistentList<Object> form;
	
	private Form(APersistentList<Object> form,APersistentSet<Symbol> deps,SourceInfo source) {
		super((deps==null)?Sets.emptySet():deps,source);
		this.form=form;
	}
	
	public static <T> Form<T> create(APersistentList<Object> form,APersistentSet<Symbol> deps,SourceInfo source) {
		return new Form<>(form,deps,source);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Form<T> create(APersistentVector<Object> form,APersistentSet<Symbol> deps,SourceInfo source) {
		if (form.size()==0) return (Form<T>) EMPTY;
		return create(Lists.coerce(form),deps,source);
	}

	public static <T> Form<T> create(magic.ast.Vector<Object> v, SourceInfo source) {
		@SuppressWarnings("unchecked")
		APersistentVector<Object> vec=(APersistentVector<Object>) ((APersistentVector<?>)v.getNodes());
		return create(Vectors.coerce(vec),v.getDependencies(),source);
	}

	public static <T> Form<T> create(APersistentVector<Object> form) {
		return create(form,null,null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Form<T> create(APersistentList<Object> form) {
		if (form.size()==0) return (Form<T>) EMPTY;
		return create(form,null,null);
	}
	
	public APersistentList<Object> getElements() {
		return form;
	}
	
//	@Override
//	public APersistentSet<Symbol> getDependencies() {
//		throw new UnsupportedOperationException("Trying to get dependencies for unexpanded form: "+form);
//	}

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

	public int size() {
		return form.size();
	}

	public Object get(int i) {
		return form.get(i);
	}

	public Object head() {
		return form.head();
	}




}
