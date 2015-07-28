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
		if ((t==this)||(t instanceof Anything)||(t instanceof Reference)) return this;
		
		// handle possible null cases
		if (t instanceof Null) return t;
		if (t instanceof Maybe) {
			Type mt=((Maybe)t).type;
			Type it = type.intersection(mt);
			if (it==type) return this;
			if (it==mt) return t;
			return Maybe.create(it);
		}
		
		return type.intersection(t);
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
		// handle optimisable Null cases
		if (t instanceof Null) return this;
		if (t instanceof Maybe) {
			Type ot=((Maybe)t).type;
			if (ot==type) return this;
			if (ot.contains(type)) return t;
			if (type.contains(ot)) return this;
			t=ot; // fall through, just consider the non-null case
		}
		if (type.contains(t)) return this;
		
		return Union.create(Null.INSTANCE,type,t);
	}
	
	@Override
	public void validate() {
		if (type.checkInstance(null)) {
			throw new TypeError("Maybe should not contain nullable type!");
		}
		type.validate();
	}
}
