package magic.type;

import magic.data.Keyword;
import magic.data.Symbol; 
import magic.Type;

/**
 * JavaType represents the type of non-null values that are of a specific Java reference type.
 * 
 * Can represent either class or interface types.
 * 
 * @author Mike
 *
 */
public class JavaType<T> extends Type {
	protected final Class<T> klass;
	
	public static final JavaType<Boolean> BOOLEAN=create(Boolean.class);
	public static final JavaType<Type> MAGIC_TYPE = create(Type.class);
	public static final JavaType<Symbol> SYMBOL = create(Symbol.class);
	public static final JavaType<Keyword> KEYWORD = create(Keyword.class);
	
	public static final JavaType<Number> NUMBER = create(Number.class);
	public static final JavaType<Long> LONG = create(Long.class);
	public static final JavaType<Integer> INTEGER = create(Integer.class);
	
	public static final JavaType<Object> OBJECT = create(Object.class);
	public static final JavaType<String> STRING = create(String.class);
	
	public JavaType(Class<T> c) {
		klass=c;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> JavaType<T> analyse(T val) {
		return new JavaType<T>((Class<T>) val.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> JavaType<T> create(Class<?> c) {
		if (c==null) throw new NullPointerException("Null Class not allowed for JavaType");
		return new JavaType<T>((Class<T>) c);
	}
	
	@Override
	public boolean checkInstance(Object o) {
		return (o!=null)&&klass.isInstance(o);
	}
	
	@Override
	public T cast(Object a) {
		return klass.cast(a);
	}

	@Override
	public Class<T> getJavaClass() {
		return klass;
	}

	@Override
	public boolean contains(Type t) {
		if ((t==this)||(t==Nothing.INSTANCE)) return true;

		if (t instanceof JavaType) {
			JavaType<?> jt=(JavaType<?>)t;
			if (klass==jt.klass) return true;
			return klass.isAssignableFrom(jt.klass);
		} else {
			return t.intersection(this)==t;
		}
	}

	@Override
	public Type intersection(Type t) {
		if ((t==this)||(t instanceof Anything)) return this;

		if ((t instanceof Null)||t==Nothing.INSTANCE) return Nothing.INSTANCE;
		if (t instanceof Maybe) {
			return ((Maybe)t).intersection(this);
		}
		if (t.getClass()==JavaType.class) { // careful to ensure exact class match! e.g. Value extends JavaType
			JavaType<?> jt=(JavaType<?>)t;
			if (this.klass==jt.klass) return this;
			if (this.contains(t)) return t;
			if (t.contains(this)) return this;
			return Nothing.INSTANCE; // TODO: what about interfaces?
		}
		return t.intersection(this);
	}
	
	@Override 
	public boolean equals(Type t) {
		if (t instanceof JavaType) {
			return klass==((JavaType<?>)t).klass;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return klass.hashCode();
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
	public boolean cannotBeTruthy() {
		return false;
	}

	@Override
	public boolean canBeFalsey() {
		return klass.isAssignableFrom(Boolean.class);
	}
	
	@Override
	public boolean cannotBeFalsey() {
		return !klass.isAssignableFrom(Boolean.class);
	}

	@Override
	public Type inverse() {
		return Not.createNew(this);
	}

	@Override
	public String toString() {
		return "(JavaType "+klass.getCanonicalName()+")";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JavaType<T> toJavaType() {
		return this;
	}

	@Override
	public Type union(Type t) {
		if (t==this) return this;
		if (t instanceof JavaType) {
			JavaType<?> jt=(JavaType<?>) t;
			if (jt.klass==this.klass) return this;
			if (jt.klass.isAssignableFrom(this.klass)) return jt;
			if (this.klass.isAssignableFrom(jt.klass)) return this;
		}
		if (t instanceof Null) {
			return Maybe.create(this);
		}
		return super.union(t);
	}
	
	@Override
	public void validate() {
		// OK
	}

	@SuppressWarnings("unchecked")
	public static <T> JavaType<T> create(T value) {
		return (JavaType<T>) create(value.getClass());
	}

}
