package magic.compiler;

import magic.lang.Context;

/**
 * Class for a combined context + value result
 * @author Mike
 *
 * @param <T>
 */
public final class Result<T> {

	private Context context;
	private T value;

	public Result(Context c, T value) {
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
		return "(Result "+context+" : "+value+")";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Result<T> create(Context c, T value) {
		return new Result(c,value);
	}
}
