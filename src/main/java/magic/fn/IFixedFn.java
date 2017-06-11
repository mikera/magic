package magic.fn;

public interface IFixedFn<T> extends IFn<T> {
	@Override
	public default boolean isFixedArity() {
		return true;
	}
	
	@Override
	public int arity();
	
	@Override
	public default boolean hasArity(int arity) {
		return arity==arity();
	}
}
