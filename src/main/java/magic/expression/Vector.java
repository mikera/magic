package magic.expression;

import magic.data.IPersistentVector;
import magic.data.PersistentHashMap;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.data.Vectors;
import magic.lang.Context;

/**
 * Expression class representing a vector construction literal.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Vector<T> extends Expression<IPersistentVector<T>> {

	IPersistentVector<Expression<T>> exps;
	
	private Vector(IPersistentVector<Expression<T>> exps) {
		this.exps=exps;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IPersistentVector<T> compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		int n=exps.size();
		if (n==0) return (IPersistentVector<T>) Tuple.EMPTY;
		T[] results=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		return Vectors.wrap(results);
	}

	public static <T> Vector<T> create(IPersistentVector<Expression<T>> exps) {
		return new Vector<T>(exps);
	}

	public IPersistentVector<Expression<T>> getExpressions() {
		return exps;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("(Vector");
		int n=exps.size();
		for (int i=0; i<n; i++) {
			sb.append(' ');
			sb.append(exps.get(i).toString());
		}
		sb.append(')');
		return sb.toString();
	}
}
