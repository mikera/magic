package magic.ast;

import magic.RT;
import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Cond<T> extends Node<T> {
	private final int nTests;
	private final Node<Object>[] tests; 
	private final Node<T>[] exps; // must be nTests+1 expressions as results
	
	private Cond(Node<Object>[] tests, Node<T>[] exs) {
		super(calcDependencies(exs).includeAll(calcDependencies(tests)));
		int n=tests.length;
		if (n==0) throw new Error("Can't create a Cond block with no tests!");
		if (exs.length!=(n+1)) throw new Error("Wrong number of results for Cond, expected "+(n+1)+ " but got "+exs.length);
		nTests=n;
		this.tests=tests;
		this.exps=exs;
	}

	@SuppressWarnings("unchecked")
	public static <T> Node<T> createIf(Node<Object> test, Node<T> trueExp) {
		return createIf(test,trueExp, (Node<T>)Constant.NULL);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Node<T> createIf(Node<Object> test, Node<T> trueExp, Node<T> falseExp) {
		return new Cond<T>(new Node[]{test},new Node[]{trueExp,falseExp});
	}

	@Override
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		Result<?> r = new Result<>(context,null);
		for (int i=0; i<nTests; i++) {
			r=tests[i].eval(r.getContext(),bindings);
			Object testVal=r.getValue();
			if (RT.bool(testVal)) {
				return exps[i].eval(r.getContext(),bindings);
			}
		}
		return exps[nTests].eval(r.getContext(),bindings);
	}

}
