package magic.fn;

@FunctionalInterface
public interface IFn5<R> extends IFixedFn<R> {

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5);
	
	@Override
	public default int arity() {
		return 5;
	}
}
