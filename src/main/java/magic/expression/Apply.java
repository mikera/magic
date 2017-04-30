package magic.expression;

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
public class Apply<T> extends Expression<T> {

	private Expression<IFn<T>> function;
	private Expression<?>[] args;
	private int arity;

	public Apply(Expression<IFn<T>> f, Expression<?>... args) {
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

	public static <T> Apply<T> create(Expression<IFn<T>> function, Expression<?>... args) {
		return new Apply<T>(function,args);
	}

	public static <T> Apply<T> create(Expression<IFn<T>> function, APersistentList<Expression<?>> tail) {
		return create(function,tail.toArray(new Expression[tail.size()]));
	}

}
