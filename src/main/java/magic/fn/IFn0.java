package magic.fn;

@FunctionalInterface
public interface IFn0<R> extends IFixedFn<R> {

	@Override
	public R apply();
	
	@Override
	public default int arity() {
		return 0;
	}
}
