package magic.ast;

import magic.data.APersistentList;
import magic.data.PersistentHashMap;
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
	public T compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		IFn<T> f=(IFn<T>) function.compute(c,bindings); // get the value of the function
		Object[] values=new Object[arity];
		for (int i=0; i<arity; i++) {
			values[i]=args[i].compute(c,bindings);
		}
		return f.applyToArray(values);
	}

	public static <T> Apply<T> create(Node<IFn<T>> function, Node<?>... args) {
		return new Apply<T>(function,args);
	}

	public static <T> Apply<T> create(Node<IFn<T>> function, APersistentList<Node<?>> tail) {
		return create(function,tail.toArray(new Node[tail.size()]));
	}

}
