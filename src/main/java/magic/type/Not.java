package magic.type;

import magic.Type;

/**
 * Represents the inverse of a given type
 * 
 * @author Mike
 */
public class Not extends Type {

	private Type type;

	private Not(Type t) {
		this.type=t;
	}
	
	public static Type create(Type t) {
		if (t instanceof Nothing) return Anything.INSTANCE;
		if (t instanceof Anything) return Nothing.INSTANCE;
		return t.inverse();
	}
	
	public static Not createNew(Type t) {
		return new Not(t);
	}

	@Override
	public boolean checkInstance(Object o) {
		return !(type.checkInstance(o));
	}

	@Override
	public Class<?> getJavaClass() {
		return Object.class;
	}

	@Override
	public boolean canBeNull() {
		return (type.cannotBeNull());
	}
	
	@Override
	public boolean cannotBeNull() {
		return (type.canBeNull());
	}

	@Override
	public boolean canBeTruthy() {
		return type.cannotBeTruthy();
	}

	@Override
	public boolean canBeFalsey() {
		return type.cannotBeFalsey();
	}

	@Override
	public boolean contains(Type t) {
		Type it=t.intersection(type);
		if (it==Nothing.INSTANCE) return true;
		return false;
	}

	@Override
	public Type intersection(Type t) {
		if (t instanceof Not) {
			Not nt=(Not)t;
			if (type.equals(nt.type)) return this;
			return nt.type.union(type).inverse();
		}		
		
		if (t==Anything.INSTANCE) return this;
		if ((t==Null.INSTANCE)||(t instanceof Maybe)||(t instanceof ValueSet)) return t.intersection(this);
		if (type.contains(t)) return Nothing.INSTANCE;
		if (type.intersection(t)==Nothing.INSTANCE) return t;
		return super.intersection(t);
	}

	@Override
	public Type inverse() {
		return type;
	}

	@Override
	public Type union(Type t) {
		Type it=t.intersection(type);
		if (it==Nothing.INSTANCE) return this;
		return super.union(t);
	}
	
	@Override
	public boolean equals(Type t) {
		if (t instanceof Not) {
			return type.equals(((Not)t).type);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "(Not "+type.toString()+")";
	}
	
	@Override
	public void validate() {
		type.validate();
	}
}
