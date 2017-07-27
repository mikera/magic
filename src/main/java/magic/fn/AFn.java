package magic.fn;

import com.oracle.truffle.api.CallTarget;

import magic.data.APersistentObject;

/**
 * Abstract base class for all Magic functions
 * 
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class AFn<T> extends APersistentObject implements IFn<T>, CallTarget {
	
	@Override
	public final Object call(Object... args) {
		return applyToArray(args);
	}
	
	@Override
	public abstract T applyToArray(Object... a);
	
	@Override
	public AFn<T> clone() {
		return this;
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}

	@Override
	public void validate() {
		// nothing to check
	}

	public boolean acceptsArgs(Object[] a) {
		return hasArity(a.length);
	}
}
