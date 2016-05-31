package magic.lang;

import magic.fn.AFn1;

/**
 * Class for a magic predicate. Supports either Boolean of boolean primitive results.
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class Predicate<T> extends AFn1<T,Boolean> implements java.util.function.Predicate<T> {

	@Override
	public Boolean apply(T a) {
		return Boolean.valueOf(test(a));
	}
	
	@Override
	public abstract boolean test(T a);
}
