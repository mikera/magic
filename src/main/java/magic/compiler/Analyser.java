package magic.compiler;

import magic.RT;
import magic.data.APersistentList;
import magic.data.IPersistentList;
import magic.data.IPersistentVector;
import magic.data.Lists;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.expression.Apply;
import magic.expression.Constant;
import magic.expression.Define;
import magic.expression.Do;
import magic.expression.Expression;
import magic.expression.Lambda;
import magic.expression.Lookup;
import magic.expression.Vector;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * Magic code analyser
 * Responsible for converting forms into AST representations
 * 
 * @author Mike
 *
 */
public class Analyser {

	public static <T> Expression<T> analyse(Object form) {
		return analyse(Context.EMPTY,form);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Expression<T>[] analyseAll(Context c, APersistentList<Object> forms) {
		int n=forms.size();
		Expression<?>[] exps=new Expression<?>[n];
		for (int i=0; i<n; i++) {
			exps[i]=analyse(c,forms.get(i));
		}
		return (Expression<T>[]) exps;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Expression<T> analyse(Context c, Object form) {
		if (form instanceof IPersistentList) return analyseList(c,(IPersistentList<Object>)form);
		if (form instanceof IPersistentVector) return (Expression<T>) analyseVector(c,(IPersistentVector<Object>)form);
		if (form instanceof Symbol) return analyseSymbol(c,(Symbol)form);
		
		// fall through handles constant literals, keywords etc
		return (Expression<T>) Constant.create(form);
	}

	@SuppressWarnings("unchecked")
	private static <T> Expression<T> analyseList(Context c, IPersistentList<Object> form) {
		int n=form.size();
		if (n==0) return (Expression<T>) Constant.create(Lists.EMPTY);
		
		Object first=form.head();
		if (first instanceof Symbol) return analyseSymbolApplication(c,(Symbol)first,form.tail());
		
		throw new Error("can't analyse form: "+RT.toString(form));
	}

	private static <T> Expression<T> analyseSymbolApplication(Context c, Symbol first, APersistentList<Object> tail) {
		if (first==Symbols.DEF) return analyseDefine(c,(Symbol)tail.head(),tail.tail());
		if (first==Symbols.FN) return analyseFn(c,tail.head(),tail.tail());
		return Apply.create(Lookup.create(first),analyseAll(c,tail));
	}



	@SuppressWarnings("unchecked")
	private static <T> Expression<T> analyseFn(Context c, Object arglist, APersistentList<Object> tail) {
		int n=tail.size();
		if (n==0) throw new Error("No function body definition");
		if (!(arglist instanceof IPersistentVector)) throw new Error("fn requires a vector argument list, got: "+arglist);
		IPersistentVector<?> args=(IPersistentVector<?>)arglist;
		
		Expression<T>[] exs=new Expression[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,tail.get(i));
		}
		
		Expression<?> doexp=Do.create(exs);
		
		return (Expression<T>) Lambda.create((IPersistentVector<Symbol>)args,doexp);
	}

	private static <T>  Expression<T> analyseDefine(Context c, Symbol sym, APersistentList<Object> args) {
		if (args.size()!=1) {
			throw new Error("Define requires exactly one agument");
		}
		return Define.create(sym,analyse(c,args.get(0)));
	}

	@SuppressWarnings("unchecked")
	private static <T> Vector<T> analyseVector(Context c, IPersistentVector<T> form) {
		int n=form.size();
		Expression<T>[] exs=new Expression[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,form.get(i));
		}
		
		IPersistentVector<Expression<T>> exps=Vectors.wrap(exs);
		return Vector.create(exps);
	}

	private static <T> Expression<T> analyseSymbol(Context c, Symbol sym) {
		return Lookup.create(sym);
	}

}
