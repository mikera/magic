package magic.fn;

@FunctionalInterface
public interface IFn3<R> extends IFn<R> {

	public R apply(Object o1, Object o2, Object o3);
	
	public default int arity() {
		return 3;
	}
}
