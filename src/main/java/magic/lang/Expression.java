package magic.lang;

/**
 * Base class for Expressions
 * @author Mike
 *
 */
public abstract class Expression<T> {

	public abstract T compute(Context c);
}
