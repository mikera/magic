package magic.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import magic.RT;
import magic.data.impl.ArraySet;
import magic.data.impl.NullSet;
import magic.data.impl.SingletonSet;

/**
 * Factory class for persistent sets
 * 
 * @author Mike Anderson
 *
 */
public class Sets {
	public static <T> APersistentSet<T> create(T value) {
		return SingletonSet.create(value);
	}
	
	public static <T> APersistentSet<T> create() {
		return emptySet();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> NullSet<T> emptySet() {
		return (NullSet<T>) NullSet.INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentSet<T> createFrom(Set<? extends T> source) {
		if (source instanceof APersistentSet<?>) {
			return create((APersistentSet<T>)source);
		}
		int size=source.size();
		if (size==0) return emptySet();
		return PersistentHashSet.createFromSet(source);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentSet<T> createFrom(Iterator<? extends T> source) {
		return (APersistentSet<T>) createFrom(RT.buildHashSet(source));
	}
	
	public static <T> APersistentSet<T> create(APersistentSet<T> source) {
		return PersistentHashSet.createFromSet(source);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentSet<T> createFrom(Collection<? extends T> source) {
		if (source instanceof Set<?>) {
			return createFrom((Set<T>)source);
		}
		return createFrom(source.iterator());
	}
	
	public static <T> APersistentSet<T> createFrom(T[] source) {
		return ArraySet.createFromArray(source);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentSet<T> concat(APersistentSet<? extends T> a, T value) {
		if (a.contains(value)) return (APersistentSet<T>) a;
		if (a.size()==0) return SingletonSet.create(value);
		return PersistentHashSet.createFromSet((APersistentSet<T>)a).include(value);
	}

	public static <T> APersistentSet<T> of(T value) {
		return PersistentHashSet.createSingleValueSet(value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentSet<T> of(T... values) {
		return createFrom(values);
	}
	
}
