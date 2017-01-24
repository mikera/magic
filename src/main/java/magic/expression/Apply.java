package magic.expression;

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
	public T compute(Context c) {
		IFn<T> f=(IFn<T>) function.compute(c);
		Object[] values=new Object[arity];
		for (int i=0; i<arity; i++) {
			values[i]=args[i].compute(c);
		}
		return f.applyToArray(values);
	}

	public static <T> Apply<T> create(Expression<IFn<T>> create, Expression<?>... args) {
		return new Apply<T>(create,args);
	}

}
