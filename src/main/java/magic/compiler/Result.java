package magic.compiler;

import magic.lang.Context;

/**
 * Class for a combined context + value result
 * @author Mike
 *
 * @param <T>
 */
public class Result<T> {

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
}
