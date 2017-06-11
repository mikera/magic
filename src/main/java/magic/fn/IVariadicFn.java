package magic.fn;

public interface IVariadicFn<T> extends IFn<T> {
	@Override
	public default boolean isFixedArity() {
		return false;
	}
	
	@Override
	public boolean hasArity(int i);
}
