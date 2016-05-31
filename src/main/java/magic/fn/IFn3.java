package magic.fn;

@FunctionalInterface
public interface IFn3<R> extends IFn<R> {

	@Override
	public R apply(Object o1, Object o2, Object o3);
	
	@Override
	public default int arity() {
		return 3;
	}
}
