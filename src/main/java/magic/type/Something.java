package magic.type;

import magic.Type;

/**
 * Type that represents any non-null reference
 * 
 * @author Mike
 */
public class Something extends Type {
	public static final Something INSTANCE=new Something();
	
	private Something() {
		// nothing to do, this is a singleton
	}
	
	@Override
	public boolean checkInstance(Object o) {
		return (o!=null);
	}
	
	@Override
	public Object cast(Object a) {
		if (a!=null) throw new ClassCastException("null cannot be cast to Something");
		return a;
	}

	@Override
	public Class<?> getJavaClass() {
		return Object.class;
	}

	@Override
	public boolean contains(Type t) {
		// just need to eliminate null possibility
		if (t.checkInstance(null)) return false;
		return true;
	}

	@Override
	public Type intersection(Type t) {
		if (t.cannotBeNull()) {
			return t;
		} else {
			return t.intersection(this);
		}
	}

	@Override
	public boolean canBeNull() {
		return false;
	}
	
	@Override
	public boolean cannotBeNull() {
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
		// TODO what about primitives?
		return Null.INSTANCE;
	}
	
	@Override
	public String toString() {
		return "Something";
	}

	@Override
	public Type union(Type t) {
		// TODO what about primitives?
		if (t.cannotBeNull()) return this;
		return Reference.INSTANCE;
	}
	
	@Override
	public void validate() {
		if (this!=Something.INSTANCE) throw new TypeError(this+ " should be a singleton!");
	}

}
