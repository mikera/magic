package magic.type;

import java.util.Collection;
import java.util.Iterator;

import magic.Type;
import magic.data.APersistentSet;
import magic.data.PersistentHashSet;

/**
 * The type of a set of 2 or more values. Values may include null.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class ValueSet<T> extends Type {
	private final PersistentHashSet<T> values;
	private final Class<T> klass;
	
	@SuppressWarnings("unchecked")
	private ValueSet(PersistentHashSet<T> values) {
		this.values=values;
		// TODO: get spanning class?
		this.klass=(Class<T>) Object.class;
	}
	
	public static <T> Type create(Collection<T> values) {
		int n=values.size();
		if (n==0) return Nothing.INSTANCE;
		if (n==1) return Value.create(values.iterator().next());
		return new ValueSet<T>(PersistentHashSet.create(values));
	}
	
	public static <T> Type create(T[] values) {
		int n=values.length;
		if (n==0) return Nothing.INSTANCE;
		if (n==1) return Value.create(values[0]);
		return new ValueSet<T>((PersistentHashSet<T>) PersistentHashSet.create(values));
	}
	
	public static <T> Type create(APersistentSet<? extends T> values) {
		int n=values.size();
		if (n==0) return Nothing.INSTANCE;
		if (n==1) return Value.create(values.seq().first());
		return new ValueSet<T>(PersistentHashSet.coerce(values));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Type of(T... values) {
		return create(values);
	}
	
	public Type update(PersistentHashSet<T> values) {
		if (values==this.values) return this;
		int n=values.size();
		if (n==1) return Value.create(values.seq().first());
		if (n==0) return Nothing.INSTANCE;
		return new ValueSet<T>(values);
	}
	
	@Override
	public boolean checkInstance(Object o) {
		return values.contains(o);
	}
	
	@Override
	public Class<T> getJavaClass() {
		return klass;
	}
	@Override
	public boolean canBeNull() {
		return values.contains(null);
	}
	
	@Override
	public boolean cannotBeNull() {
		return !values.contains(null);
	}
	
	@Override
	public boolean canBeTruthy() {
		return true;
	}
	
	@Override
	public boolean canBeFalsey() {
		return (values.contains(Boolean.FALSE))||(values.contains(null));
	}
	
	@Override
	public boolean cannotBeTruthy() {
		return false;
	}
	
	@Override
	public boolean cannotBeFalsey() {
		return !((values.contains(Boolean.FALSE))||(values.contains(null)));
	}
	
	@Override
	public boolean contains(Type t) {
		if (t==this) return true;
		if (t instanceof Nothing) return true;
		if (t instanceof Value) {
			Value<?> ev=(Value<?>) t;
			return values.contains(ev.value);
		}
		if (t instanceof ValueSet) {
			@SuppressWarnings("rawtypes")
			PersistentHashSet<?> tvs=((ValueSet) t).values;
			return tvs.containsAll(values);
		}

		return false;
	}

	@Override
	public Type inverse() {
		return Not.createNew(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Type union(Type t) {
		if (t==this) return t;
		if (t instanceof Value) {
			Object value=((Value<?>)t).value;
			if (values.contains(value)) {
				return this;
			} else {
				return update(values.include((T) value));
			}
		}
		return super.union(t);
	}
	
	@Override
	public Type intersection(Type t) {
		if (t==this) return t;
		PersistentHashSet<T> vals=values;
		Iterator<T> it=values.iterator();
		while(it.hasNext()) {
			T v=it.next();
			if (!t.checkInstance(v)) vals=vals.exclude(v);
		}
		return update(vals);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Type t) {
		if (t instanceof ValueSet) {
			return values.equals(((ValueSet<T>)t).values);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return values.hashCode();
	}
	
	@Override
	public String toString() {
		return "(Values "+values.toString()+")";
	}
	
	@Override
	public void validate() {
		if (values.size()<=1) throw new TypeError("Insufficient values in ValueSet!");
		
		// TODO: class tests?
	}




}
