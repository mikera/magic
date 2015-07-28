package magic.fn;

@FunctionalInterface
public interface IFn1<R> extends IFn<R> {

	public R apply(Object o1);
	
	public default int arity() {
		return 1;
	}
}
