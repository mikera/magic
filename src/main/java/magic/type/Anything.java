package magic.type;

import magic.Type;

/**
 * A type that represents any value, including null. Every type is a subclass of Anything.
 * 
 * @author Mike
 */
public class Anything extends Type {
	public static final Anything INSTANCE=new Anything();

	@Override
	public boolean checkInstance(Object o) {
		return true;
	}
	
	@Override
	public Object cast(Object a) {
		return a;
	}

	@Override
	public Class<?> getJavaClass() {
		return Object.class;
	}

	@Override
	public boolean contains(Type t) {
		return true;
	}

	@Override
	public Type intersection(Type t) {
		return t;
	}

	@Override
	public boolean canBeNull() {
		return true;
	}

	@Override
	public boolean canBeTruthy() {
		return true;
	}

	@Override
	public boolean canBeFalsey() {
		return true;
	}

	@Override
	public Type inverse() {
		return Nothing.INSTANCE;
	}

	@Override
	public Type union(Type t) {
		return this;
	}

	@Override
	public void validate() {
		if (this!=Anything.INSTANCE) throw new Error(this+ " should be a singleton!");
	}

}
