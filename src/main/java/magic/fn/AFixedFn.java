package magic.fn;

public abstract class AFixedFn<T> extends AFn<T> {
	
	private final int arity;	
	
	protected AFixedFn(int arity) {
		this.arity=arity;
	}
	
	@Override
	public boolean hasArity(int a) {

		return a==arity;
	}

	@Override
	public abstract T applyToArray(Object... a);

}
