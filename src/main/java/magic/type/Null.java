package magic.type;

import magic.Type;

/**
 * The type of the value null
 * 
 * @author Mike
 *
 */
public class Null extends Type {

	public static final Null INSTANCE = new Null();
	
	private Null() {
	}

	@Override
	public boolean checkInstance(Object o) {
		return o==null;
	}
	
	@Override
	public Object cast(Object a) {
		if (a!=null) throw new ClassCastException("Can't cast non-null object to null");
		return null;
	}

	@Override
	public Class<Object> getJavaClass() {
		return Object.class;
	}
	
	@Override
	public Type getReturnType() {
		return null;
	}

	@Override
	public boolean contains(Type t) {
		if ((t==INSTANCE)||(t==Nothing.INSTANCE)) return true;
		if (t.canBeTruthy()) return false;
		return false;
	}

	@Override
	public Type intersection(Type t) {
		if (t.checkInstance(null)) return this;
		return Nothing.INSTANCE;
	}

	@Override
	public boolean canBeNull() {
		return true;
	}

	@Override
	public boolean canBeTruthy() {
		return false;
	}

	@Override
	public boolean cannotBeTruthy() {
		return true;
	}
	
	@Override
	public boolean canBeFalsey() {
		return true;
	}

	@Override
	public Type inverse() {
		// TODO: what about primitives?
		return Something.INSTANCE;
	}

	@Override
	public Type union(Type t) {
		if (t.checkInstance(null)) return t;
		return Maybe.create(t);
	}
	
	@Override
	public void validate() {
		if (this!=Null.INSTANCE) throw new TypeError(this+ " should be a singleton!");
	}
	
	@Override
	public String toString() {
		return "Null";
	}

}
