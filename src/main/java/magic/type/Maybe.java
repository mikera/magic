package magic.type;

import magic.Type;

/**
 * Maybe type, represents the type of values that may be either null or non-null values of another type
 * 
 * Specialises Reference to a specific subset of non-null types
 * 
 * @author Mike
 *
 */
public class Maybe extends Type {	
	Type type;

	private Maybe(Type t) {
		this.type=t;
	}
	
	public static Type create(Type t) {
		if ((t instanceof Null)||(t instanceof Nothing)) {
			return Null.INSTANCE;
		}
		if (t.checkInstance(null)) {
			return t;
		}
		if (t instanceof Something) {
			return Reference.INSTANCE;
		}
		return new Maybe(t.intersection(Reference.INSTANCE));
	}

	@Override
	public boolean checkInstance(Object o) {
		return (o==null)||type.checkInstance(o);
	}

	@Override
	public Class<?> getJavaClass() {
		return type.getJavaClass();
	}
	
	@Override
	public Type getReturnType() {
		return type.getReturnType();
	}

	@Override
	public boolean contains(Type t) {
		if (t==this) return true;
		
		if (t instanceof Maybe) {
			return type.contains(((Maybe)t).type);
		}
		return Null.INSTANCE.contains(t)||type.contains(t);
	}

	@Override
	public Type intersection(Type t) {
		if (t.checkInstance(null)) {
			// return type includes null
			Type nt=type.intersection(t);
			if (nt==type) return this;
			return Maybe.create(nt);
		} else {
			// return type excludes null
			return t.intersection(type);
		}
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
		return Not.createNew(this);
	}

	@Override
	public Type union(Type t) {
		return Maybe.create(type.union(t));
	}
	
	@Override
	public String toString() {
		return "(Maybe "+type.toString()+")";
	}
	
	@Override
	public void validate() {
		if (type.checkInstance(null)) {
			throw new TypeError("Maybe should not contain nullable type!");
		}
		type.validate();
	}
}
