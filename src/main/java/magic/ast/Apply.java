package magic.ast;

import magic.compiler.Result;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.fn.IFn;
import magic.lang.Context;

/**
 * Expression representing a function application
 * @author Mike
 *
 */
public class Apply<T> extends Node<T> {

	private Node<IFn<T>> function;
	private Node<?>[] args;
	private int arity;

	public Apply(Node<IFn<T>> f, Node<?>... args) {
		super(calcDependencies(f,args));
		this.function=f;
		this.args=args;
		arity=args.length;
	}

	@Override
	public Result<T> compile(Context c,APersistentMap<Symbol,?> bindings) {
		Result<IFn<T>> rf= function.compile(c,bindings);
		IFn<T> f=rf.getValue();
		Object[] values=new Object[arity];
		Result<?> r=rf;
		for (int i=0; i<arity; i++) {
			r=args[i].compile(c,bindings);
			values[i]=r.getValue();
		}
		return Result.create(r.getContext(),f.applyToArray(values));
	}

	public static <T> Apply<T> create(Node<IFn<T>> function, Node<?>... args) {
		return new Apply<T>(function,args);
	}

	public static <T> Apply<T> create(Node<IFn<T>> function, APersistentList<Node<?>> tail) {
		return create(function,tail.toArray(new Node[tail.size()]));
	}

}
