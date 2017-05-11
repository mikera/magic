package magic.lang;

import magic.Type;
import magic.Types;
import magic.fn.AFn1;

/**
 * Class for a magic predicate. Supports either Boolean of boolean primitive results.
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class Predicate<T> extends AFn1<T,Boolean> implements java.util.function.Predicate<T> {

	@SuppressWarnings("unchecked")
	@Override
	public Boolean apply(Object a) {
		return Boolean.valueOf(test((T)a));
	}
	
	@Override
	public abstract boolean test(T a);
	
	@Override
	public Type getType() {
		return Types.PREDICATE;
	}
}
