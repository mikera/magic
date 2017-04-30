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
	public static <T> PersistentSet<T> create(T value) {
		return SingletonSet.create(value);
	}
	
	public static <T> PersistentSet<T> create() {
		return emptySet();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> NullSet<T> emptySet() {
		return (NullSet<T>) NullSet.INSTANCE;
	}
	
	public static <T> PersistentSet<T> createFrom(Set<T> source) {
		if (source instanceof PersistentSet<?>) {
			return create((PersistentSet<T>)source);
		}
		int size=source.size();
		if (size==0) return emptySet();
		return PersistentHashSet.createFromSet(source);
	}
	
	public static <T> PersistentSet<T> createFrom(Iterator<T> source) {
		return createFrom(RT.buildHashSet(source));
	}
	
	public static <T> PersistentSet<T> create(PersistentSet<T> source) {
		return PersistentHashSet.createFromSet(source);
	}
	
	public static <T> PersistentSet<T> createFrom(Collection<T> source) {
		if (source instanceof Set<?>) {
			return createFrom((Set<T>)source);
		}
		return createFrom(source.iterator());
	}
	
	public static <T> PersistentSet<T> createFrom(T[] source) {
		return ArraySet.createFromArray(source);
	}
	
	public static <T> PersistentSet<T> concat(PersistentSet<T> a, T value) {
		if (a.contains(value)) return a;
		if (a.size()==0) return SingletonSet.create(value);
		return PersistentHashSet.createFromSet(a).conj(value);
	}
	
}
