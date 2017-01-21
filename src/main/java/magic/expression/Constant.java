package magic.expression;

import magic.lang.Context;
import magic.lang.Expression;

/**
 * Expression representing a constant.
 * 
 * @author Mike
 *
 */
public class Constant<T> extends Expression<T> {

	private final T value;
	
	public Constant(T value) {
		this.value=value;
	}
	
	@Override
	public T compute(Context c) {
		return value;
	}

	public static <T> Constant<T> create(T v) {
		return new Constant<T>(v);
	}

}
