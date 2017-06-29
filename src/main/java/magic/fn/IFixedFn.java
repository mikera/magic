package magic.fn;

/**
 * Interface for fixed arity functions
 * @author Mike
 *
 * @param <T>
 */
public interface IFixedFn<T> extends IFn<T> {
	@Override
	public default boolean isVariadic() {
		return false;
	}
	
	@Override
	public int arity();
	
	@Override
	public default boolean hasArity(int arity) {
		return arity==arity();
	}
}
