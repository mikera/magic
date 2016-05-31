package magic.fn;

@FunctionalInterface
public interface IFn1<R> extends IFn<R> {

	@Override
	public R apply(Object o1);
	
	@Override
	public default int arity() {
		return 1;
	}
}
