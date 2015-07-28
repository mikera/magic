package magic.fn;

@FunctionalInterface
public interface IFn2<R> extends IFn<R> {

	public R apply(Object o1, Object o2);
	
	public default int arity() {
		return 2;
	}
}
