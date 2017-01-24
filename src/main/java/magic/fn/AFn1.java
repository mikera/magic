package magic.fn;

import magic.RT;
import java.util.function.Predicate;

import java.util.function.Function;

public abstract class AFn1<T, R> extends AFn<R> implements Function<T, R>, Predicate<T> {
	@Override
	public abstract R apply(Object a);

	@Override
	public boolean test(T a) {
		return RT.bool(apply(a));
	}
}
