package magic.ast;

import magic.data.IPersistentVector;
import magic.data.PersistentHashMap;
import magic.data.Symbol;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn;
import magic.lang.Context;

public class Lambda<T> extends Node<IFn<T>> {

	private final IPersistentVector<Symbol> args;
	private final Node<T> body;
	private final int arity;

	public Lambda(IPersistentVector<Symbol> args, Node<T> body) {
		super(body.getDependencies().excludeAll(args));
		this.args=args;
		this.arity=args.size();
		this.body=body;
	}

	@Override
	public IFn<T> compute(Context context,PersistentHashMap<Symbol,?> bindings) {
		return new AFn<T>() {
			@Override
			public T applyToArray(Object... a) {
				if (a.length!=arity) throw new ArityException(arity,a.length);
				Context c=context;
				for (int i=0; i<arity; i++) {
					c=c.define(args.get(i), Constant.create(a[i]));
				}
				return body.compute(c,bindings);
			}	
		};
	}

	public static <T> Lambda<T> create(IPersistentVector<Symbol> args, Node<T> body) {
		return new Lambda<T>(args,body);
	}

}
