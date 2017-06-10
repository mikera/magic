package magic.fn;

import com.oracle.truffle.api.CallTarget;

import magic.Type;
import magic.Types;
import magic.data.APersistentObject;
import magic.type.FunctionType;

/**
 * Abstract base class for Magic functions
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
	public Type getType() {
		return FunctionType.create(Types.ANYTHING);
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}

	@Override
	public void validate() {
		// nothing to check
	}
}
