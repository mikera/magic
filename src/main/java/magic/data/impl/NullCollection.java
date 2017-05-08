package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;

import magic.data.ISeq;
import magic.data.APersistentCollection;

/**
 * Immutable empty collection type and singleton
 */
public final class NullCollection<T> extends APersistentCollection<T> {
	private static final long serialVersionUID = 2925953822669265599L;

	@SuppressWarnings("rawtypes")
	public static NullCollection<?> INSTANCE=new NullCollection();
	
	protected NullCollection() {
		
	}
	
	@Override
	public final T valAt(Object key) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final T valAt(Object key,Object notFound) {
		return (T) notFound;		
	}

	
	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if (c.isEmpty()) return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return (Iterator<T>)NullIterator.INSTANCE;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object[] toArray() {
		return EmptyArrays.EMPTY_OBJECTS;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[])toArray();
	}

	@Override
	public APersistentCollection<T> exclude(T value) {
		return this;
	}

	@Override
	public APersistentCollection<T> excludeAll(Collection<T> values) {
		return this;
	}
	
	@Override
	public APersistentCollection<T> clone() {
		return this;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object a) {
		return ((a instanceof APersistentCollection<?>)&&((APersistentCollection<T>)a).isEmpty());
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}

	@Override
	public APersistentCollection<T> include(T value) {
		return RepeatVector.create(value,1);
	}

	@Override
	public ISeq<T> seq() {
		return null;
	}

	@Override
	public APersistentCollection<T> empty() {
		return this;
	}

	@Override
	public String toString() {
		return "()";
	}
}
