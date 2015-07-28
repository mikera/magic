package magic;

import clojure.lang.Symbol;
import magic.type.*;

/**
 * Abstract base class for Kiss types
 * 
 * @author Mike
 *
 */
public abstract class Type {	
	public static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
	
	/**
	 * Performs a runtime check if an object is an instance of this type
	 * 
	 * @param o
	 * @return
	 */
	public abstract boolean checkInstance(Object o);
	
	/**
	 * Returns the most specific Java class or interface that can represent all instances of this type
	 * @return
	 */
	public abstract Class<?> getJavaClass();
	
	/**
	 * Returns true if this is a JVM primitive type
	 * @return
	 */
	public boolean isPrimitive() {
		return false;
	}
	
	/**
	 * Return true if this type provably contains the null value
	 * @return
	 */
	public boolean canBeNull() {
		return checkInstance(null);
	}
	
	/**
	 * Returns true if this type provably contains at least one truthy value
	 * @return
	 */
	public abstract boolean canBeTruthy();
	
	/**
	 * Returns true if this type provably contains at least one falsey value
	 * @return
	 */
	public abstract boolean canBeFalsey();

	/**
	 * Returns true if another type t is provably contained within this type.
	 * 
	 * Equivalently this means:
	 * - t is a subtype of this type
	 * - every instance of t is an instance of this type
	 * 
	 * If contains returns false, t may still be a subtype - it just can't be proven
	 *  
	 * @param t
	 * @return
	 */
	public abstract boolean contains(Type t);

	/**
	 * Returns the intersection of this type with another type
	 * @param t
	 * @return
	 */
	public Type intersection(Type t) {
		return Intersection.create(this,t);
	}
	
	/**
	 * Returns the union of this type with another type
	 * @param t
	 * @return
	 */
	public Type union(Type t) {
		return Union.create(this,t);
	}

	/**
	 * Returns true if this type can be proven to equal another type.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if (o==this) return true;
		if (!(o instanceof Type)) return false;
		return equals((Type)o);
	}
	
	public boolean equals(Type t) {
		return t.contains(this)&&this.contains(t);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public Object cast(Object a) {
		if (!checkInstance(a)) throw new ClassCastException("Can't cast value to type "+this.toString());
		return a;
	}	
	
	public abstract void validate();

	public abstract Type inverse();

	public boolean cannotBeNull() {
		return !checkInstance(null);
	}

	public boolean cannotBeFalsey() {
		return !(checkInstance(null)||checkInstance(Boolean.FALSE));
	}

	/**
	 * Returns true if the type provably cannot be a true value (i.e. must be null or Boolean.FALSE)
	 * @return
	 */
	public boolean cannotBeTruthy() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> JavaType<T> toJavaType() {
		return (JavaType<T>) JavaType.create(this.getJavaClass());
	}

	/**
	 * Gets the return type of instances of this type
	 * Returns null if the Type does not represent a function 
	 * @return
	 */
	public Type getReturnType() {
		return null;
	}
}
