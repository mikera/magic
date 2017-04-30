package magic.analyser;

import magic.data.IPersistentVector;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.expression.Constant;
import magic.expression.Expression;
import magic.expression.Lookup;
import magic.expression.Vector;
import magic.lang.Context;

public class Analyser {

	public static <T> Expression<T> analyse(Object form) {
		return analyse(Context.EMPTY,form);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Expression<T> analyse(Context c, Object form) {
		if (form instanceof IPersistentVector) return (Expression<T>) analyseVector(c,(IPersistentVector<Object>)form);
		if (form instanceof Symbol) return analyseSymbol(c,(Symbol)form);
		return (Expression<T>) Constant.create(form);
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
