package magic.fn;

public interface IVariadicFn<T> extends IFn<T> {
	@Override
	public default boolean isVariadic() {
		return true;
	}
	
	@Override
	public boolean hasArity(int i);
}
