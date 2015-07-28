package magic.fn;

import magic.RT;
import java.util.function.Predicate;
import java.util.function.Function;

public abstract class AFn1<T, R> implements Function<T, R>, Predicate<T> {
	public abstract R apply(T a);

	public boolean test(T a) {
		return RT.bool(apply(a));
	}
}
