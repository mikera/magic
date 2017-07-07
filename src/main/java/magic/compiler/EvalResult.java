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
	private final int returnType;

	public static final int NORMAL=0;
	public static final int RETURN=1;
	public static final int RECUR=2;
	
	public EvalResult(Context c, T value) {
		this(c,value,0);
	}
	
	public EvalResult(Context c, T value, int returnType) {
		this.context=c;
		this.value=value;
		this.returnType=returnType;
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
		return returnType>0;
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
		return new EvalResult<T>(c,(T)r,1);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> recurValues(Context c,Object[] args) {
		return new EvalResult<T>(c,(T)args,2);
	}

	public boolean isRecurring() {
		return returnType==RECUR;
	}
}
