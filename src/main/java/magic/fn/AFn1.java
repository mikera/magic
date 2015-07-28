package magic.fn;

import magic.RT;

public abstract class AFn1<T,R> implements java.util.function.Function<T,R> {
	public abstract R apply(T a);
	
	public boolean test(T a) {
		return RT.bool(apply(a));
	}
}
