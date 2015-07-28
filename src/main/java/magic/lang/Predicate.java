package magic.lang;

/**
 * Class for a magic predicate. Supports either Boolean of boolean primitive results.
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class Predicate<T> extends Fn1<T,Boolean> implements java.util.function.Predicate<T> {

	public Boolean apply(T a) {
		return Boolean.valueOf(test(a));
	}
	
	public abstract boolean test(T a);
}
