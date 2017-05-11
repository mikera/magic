package magic.fn;

import com.oracle.truffle.api.CallTarget;

import magic.Type;
import magic.Types;
import magic.data.IPersistentObject;
import magic.type.FunctionType;

public abstract class AFn<T> implements IFn<T>, CallTarget, IPersistentObject {
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
