package magic.expression;

import magic.data.PersistentHashMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Do<T> extends Expression<T> {
	private final Expression<?>[] exps;
	
	public Do(Expression<?>[] exs) {
		exps=exs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		int n=exps.length;
		for (int i=0; i<(n-1); i++) {
			exps[i].compute(c,bindings);
		}
		return (T) exps[n-1].compute(c,bindings);
	}

	public static <T> Expression<T> create(Expression<?>[] exs) {
		return new Do<T>(exs);
	}

}
