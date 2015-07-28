package magic.fn;

@FunctionalInterface
public interface IFn0<R> extends IFn<R> {

	public R apply();
	
	public default int arity() {
		return 0;
	}
}
