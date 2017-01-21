package magic.expression;

import magic.fn.IFn;
import magic.lang.Context;
import magic.lang.Expression;

/**
 * Expression representing a function application
 * @author Mike
 *
 */
public class Apply extends Expression {

	private Expression function;
	private Expression[] args;
	private int arity;

	public Apply(Expression f, Expression... args) {
		this.function=f;
		this.args=args;
		arity=args.length;
	}
	
	@Override
	public Object compute(Context c) {
		IFn<?> f=(IFn<?>) function.compute(c);
		Object[] values=new Object[arity];
		for (int i=0; i<arity; i++) {
			values[i]=args[i].compute(c);
		}
		return f.applyToArray(values);
	}

}
