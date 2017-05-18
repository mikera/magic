package magic.fn;

import java.util.function.Predicate;

import magic.RT;

public abstract class AFn1<T, R> extends AFn<R> implements Predicate<T> {
	@Override
	public abstract R apply(Object a);

	@Override
	public boolean test(T a) {
		return RT.bool(apply(a));
	}

	@Override
	public R applyToArray(Object... a) {
		if (a.length!=1) throw new ArityException(1,a.length);
		return apply(a[1]);
	}
}
