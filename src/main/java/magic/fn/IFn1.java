package magic.fn;

/**
 * 
 * @author Mike
 *
 * @param <T> The type of inputs to the function
 * @param <R> The return type of the function
 */
@FunctionalInterface
public interface IFn1<T,R> extends IFixedFn<R> {

	@Override
	public R apply(Object o);
	
	@Override
	public default int arity() {
		return 1;
	}
}
