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

	private final Context context;
	private final T value;
	private final boolean isReturn;

	public EvalResult(Context c, T value) {
		this(c,value,false);
	}
	
	public EvalResult(Context c, T value, boolean isReturn) {
		this.context=c;
		this.value=value;
		this.isReturn=isReturn;
	}

	public Context getContext() {
		return context;
	}
	
	public T getValue() {
		return value;
	}
	
	/**
	 * Returns true if the result of this evaluation is escaping outside normal control flow.
	 * 
	 * This may be:
	 * - a function 'return' value
	 * - a loop 'recur' value
	 *  
	 * @return
	 */
	public boolean isEscaping() {
		return isReturn;
	}
	
	@Override
	public String toString() {
		return "(Result "+context+" : "+RT.toString(value)+")";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> EvalResult<T> create(Context c, T value) {
		return new EvalResult(c,value);
	}

	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> returnValue(Context c,Object r) {
		return new EvalResult<T>(c,(T)r,true);
	}
}
