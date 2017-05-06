package magic.compiler;

import magic.RT;
import magic.ast.Apply;
import magic.ast.Constant;
import magic.ast.Define;
import magic.ast.Do;
import magic.ast.Form;
import magic.ast.Node;
import magic.ast.Lambda;
import magic.ast.Lookup;
import magic.ast.Vector;
import magic.data.APersistentList;
import magic.data.IPersistentList;
import magic.data.IPersistentVector;
import magic.data.Lists;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.fn.IFn3;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.Symbols;

/**
 * Magic code analyser
 * Responsible for converting forms into AST representations
 * 
 * @author Mike
 *
 */
public class Analyser {

	/**
	 * Analyses a form in an empty context. Useful mainly for debug / test purposes
	 * @param form
	 * @return
	 */
	public static <T> Node<T> analyse(Object form) {
		return analyse(RT.INITIAL_CONTEXT,form);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Node<T>[] analyseAll(Context c, APersistentList<Object> forms) {
		int n=forms.size();
		Node<?>[] exps=new Node<?>[n];
		for (int i=0; i<n; i++) {
			exps[i]=analyse(c,forms.get(i));
		}
		return (Node<T>[]) exps;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Node<T> analyse(Context c, Object form) {
		if (form instanceof IPersistentList) return analyseList(c,(IPersistentList<Object>)form);
		if (form instanceof IPersistentVector) return (Node<T>) analyseVector(c,(IPersistentVector<Object>)form);
		if (form instanceof Symbol) return analyseSymbol(c,(Symbol)form);
		
		// fall through handles constant literals, keywords etc
		return (Node<T>) Constant.create(form);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseList(Context c, IPersistentList<Object> form) {
		int n=form.size();
		if (n==0) return (Node<T>) Constant.create(Lists.EMPTY);
		
		Object first=form.head();
		if (first instanceof Symbol) return analyseSymbolApplication(c,form);
		
		throw new Error("can't analyse form: "+RT.toString(form));
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseSymbolApplication(Context c, IPersistentList<Object> form) {
		Symbol first=(Symbol) form.head();
		APersistentList<Object> tail=form.tail();
		
		if (first==Symbols.DEF) return analyseDefine(c,(Symbol)tail.head(),tail.tail());
		if (first==Symbols.FN) return analyseFn(c,tail.head(),tail.tail());
		
		Slot<?> slot=c.getSlot(first);
		if (slot==null) {
			// we have a form that we don't yet know how to expand
			return (Node<T>) Form.create(form,first);
		}
		
		if (slot.isExpander(c)) {
			IFn3<Node<T>> ex=(IFn3<Node<T>>) slot.getValue(c);
			return ex.apply(c,form,Expanders.INITAL_EXPANDER);
		}
		
		return Apply.create(Lookup.create(first),analyseAll(c,tail));
	}



	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseFn(Context c, Object arglist, APersistentList<Object> tail) {
		int n=tail.size();
		if (n==0) throw new Error("No function body definition");
		if (!(arglist instanceof IPersistentVector)) throw new Error("fn requires a vector argument list, got: "+arglist);
		IPersistentVector<?> args=(IPersistentVector<?>)arglist;
		
		Node<T>[] exs=new Node[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,tail.get(i));
		}
		
		Node<?> doexp=Do.create(exs);
		
		return (Node<T>) Lambda.create((IPersistentVector<Symbol>)args,doexp);
	}

	private static <T>  Node<T> analyseDefine(Context c, Symbol sym, APersistentList<Object> args) {
		if (args.size()!=1) {
			throw new Error("Define requires exactly one agument");
		}
		return Define.create(sym,analyse(c,args.get(0)));
	}

	@SuppressWarnings("unchecked")
	private static <T> Vector<T> analyseVector(Context c, IPersistentVector<T> form) {
		int n=form.size();
		Node<T>[] exs=new Node[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,form.get(i));
		}
		
		IPersistentVector<Node<T>> exps=Vectors.wrap(exs);
		return Vector.create(exps);
	}

	private static <T> Node<T> analyseSymbol(Context c, Symbol sym) {
		return Lookup.create(sym);
	}

}
