package magic.ast;

import magic.RT;
import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Let<T> extends Node<T> {
	private final int n;
	private final Node<?>[] body;
	private final Symbol[] syms;
	private final Node<?>[] lets;
	
	public Let(Node<?>[] bodyExprs, Symbol[] syms, Node<?>[] lets) {
		super(calcDependencies(bodyExprs).excludeAll(syms));
		n=syms.length;
		if (n!=lets.length) throw new IllegalArgumentException("Incorrect number of bindings forms for let");
		this.syms=syms;
		this.lets=lets;
		body=bodyExprs;
	}

	public static <T> Node<T> create(Node<?>[] body) {
		return create(RT.EMPTY_SYMBOLS,RT.EMPTY_NODES,body);
	}
	
	public static <T> Node<T> create(Symbol[] syms,Node<?>[] lets,Node<?>[] body) {
		return new Let<T>(body,syms,lets);
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		int nBody=body.length;
		
		for (int i=0; i<n; i++) {
			bindings=bindings.assoc(syms[i], (Object)(lets[i].compute(context)));
		}
		
		Result<T> r=new Result<>(context,null);
		for (int i=0; i<nBody; i++) {
			r=(Result<T>) body[i].eval(r.getContext(),bindings);
		}
		return r;
	}

}
