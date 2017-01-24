package magic.fn;

import com.oracle.truffle.api.CallTarget;

public abstract class AFn<T> implements IFn<T>, CallTarget {
	@Override
	public final Object call(Object... args) {
		return applyToArray(args);
	}
}
