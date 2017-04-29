package magic.expression;

import magic.RT;
import magic.lang.Context;

/**
 * Expression representing a constant.
 * 
 * @author Mike
 *
 */
public class Constant<T> extends BaseConstant<T> {

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
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "(Constant "+RT.print(value)+")";
	}

}
