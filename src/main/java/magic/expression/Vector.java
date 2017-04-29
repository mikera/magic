package magic.expression;

import magic.data.IPersistentList;
import magic.data.ListFactory;
import magic.lang.Context;

public class Vector<T> extends Expression<IPersistentList<T>> {

	IPersistentList<Expression<T>> exps;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector(IPersistentList<Expression<?>> exps) {
		this.exps=(IPersistentList)exps;
	}
	
	@Override
	public IPersistentList<T> compute(Context c) {
		int n=exps.size();
		@SuppressWarnings("unchecked")
		T[] results=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c);
		}
		return ListFactory.createFromArray(results);
	}

	public static Expression<?> create(IPersistentList<Expression<?>> exps) {
		return new Vector<Object>(exps);
	}

	public IPersistentList<Expression<T>> getExpressions() {
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
