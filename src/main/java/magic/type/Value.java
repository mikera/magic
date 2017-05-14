package magic.type;

import magic.RT;
import magic.Type;
import magic.lang.Tools;

/**
 * The type of a specific non-null value
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Value<T> extends JavaType<T> {
	final T value;
	
	@SuppressWarnings("unchecked")
	private Value(T value) {
		super((Class<T>) value.getClass());
		this.value=value;
		
	}
	
	public static <T> Value<T> create(T value) {
		return new Value<T>(value);
	}
	
	public static <T> Type of(T value) {
		return create(value);
	}
	
	@Override
	public boolean checkInstance(Object o) {
		return value.equals(o);
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
		return value!=Boolean.FALSE;
	}
	
	@Override
	public boolean canBeFalsey() {
		return value==Boolean.FALSE;
	}
	
	@Override
	public boolean cannotBeTruthy() {
		return value==Boolean.FALSE;
	}
	
	@Override
	public boolean cannotBeFalsey() {
		return value!=Boolean.FALSE;
	}
	
	@Override
	public boolean contains(Type t) {
		if (t==this) return true;
		if (t instanceof Nothing) return true;
		if (t instanceof Value) {
			Value<?> ev=(Value<?>) t;
			if (ev.klass!=this.klass) return false;
			return ev.value.equals(this.value);
		}
		return false;
	}
	
	@Override
	public Type intersection(Type t) {
		if (t.checkInstance(value)) return this;
		return Nothing.INSTANCE;
	}

	@Override
	public Type inverse() {
		return Not.createNew(this);
	}

	@Override
	public Type union(Type t) {
		if (t==this) return t;
		if (t.checkInstance(value)) return t;
		
		if (t instanceof Value) {
			Object tv=((Value<?>)t).value;
			if (tv.equals(this.value)) return this;
			return ValueSet.create(new Object[] {value,tv});
		}
		if (t instanceof ValueSet) {
			return t.union(this);
		}
		
		return super.union(t);
	}
	
	@Override
	public boolean equals(Type t) {
		if (t instanceof Value) {
			Value<?> v=(Value<?>) t;
			if (Tools.equals(v.value,this.value)) return true;
			return false;
		}
		return super.equals(t);
	}
	
	@Override
	public String toString() {
		return "(Value "+RT.toString(value)+")";
	}
	
	@Override
	public void validate() {
		if (!(klass.isInstance(value))) throw new TypeError(value+ " is of wrong type, should be "+klass);
	}




}
