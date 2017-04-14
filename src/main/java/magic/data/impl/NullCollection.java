package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;

import magic.data.ISeq;
import magic.data.PersistentCollection;

/**
 * Immutable empty collection type and singleton
 */
public final class NullCollection<T> extends PersistentCollection<T> {
	private static final long serialVersionUID = 2925953822669265599L;

	@SuppressWarnings("rawtypes")
	public static NullCollection<?> INSTANCE=new NullCollection();
	
	protected NullCollection() {
		
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
	public PersistentCollection<T> delete(T value) {
		return this;
	}

	@Override
	public PersistentCollection<T> deleteAll(Collection<T> values) {
		return this;
	}
	
	@Override
	public PersistentCollection<T> clone() {
		return this;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object a) {
		return ((a instanceof PersistentCollection<?>)&&((PersistentCollection<T>)a).isEmpty());
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}

	@Override
	public PersistentCollection<T> conj(T value) {
		return SingletonList.of(value);
	}

	@Override
	public ISeq<T> seq() {
		return null;
	}
}
