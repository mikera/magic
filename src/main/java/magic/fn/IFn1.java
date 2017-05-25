package magic.fn;

@FunctionalInterface
public interface IFn1<T,R> extends IFixedFn<R> {

	@Override
	public R apply(Object o);
	
	@Override
	public default int arity() {
		return 1;
	}
}
