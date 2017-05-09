package magic.compiler;

import magic.RT;
import magic.lang.Context;

/**
 * Class for a combined context + value result from eval
 * 
 * In principle, eval during compilation stage may change the context at any time so we need this class
 * as a return value to compute the overall results.
 * 
 * @author Mike
 *
 * @param <T>
 */
public final class EvalResult<T> {

	private Context context;
	private T value;

	public EvalResult(Context c, T value) {
		this.context=c;
		this.value=value;
	}
	
	public Context getContext() {
		return context;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "(Result "+context+" : "+RT.toString(value)+")";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> EvalResult<T> create(Context c, T value) {
		return new EvalResult(c,value);
	}
}
