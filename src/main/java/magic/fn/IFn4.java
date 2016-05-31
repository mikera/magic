package magic.fn;

@FunctionalInterface
public interface IFn4<R> extends IFn<R> {

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4);
	
	@Override
	public default int arity() {
		return 4;
	}
}
