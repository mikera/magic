package magic.ast;

import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Do<T> extends Node<T> {
	private final Node<?>[] exps;
	
	public Do(Node<?>[] exs) {
		super(calcDependencies(exs));
		exps=exs;
	}

	public static <T> Node<T> create(Node<?>[] exs) {
		return new Do<T>(exs);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Result<T> compile(Context context, APersistentMap<Symbol,?> bindings) {
		int n=exps.length;
		Result<T> r=new Result<>(context,null);
		for (int i=0; i<n; i++) {
			r=(Result<T>) exps[i].compile(r.getContext(),bindings);
		}
		return r;
	}

}
