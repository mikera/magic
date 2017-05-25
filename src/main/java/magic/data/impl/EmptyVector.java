package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import magic.data.ISeq;
import magic.data.Vectors;
import magic.data.APersistentVector;

public final class EmptyVector<T> extends APersistentVector<T> {
	
	private static final long serialVersionUID = -268387358134950528L;

	@SuppressWarnings("rawtypes")
	public static EmptyVector<?> INSTANCE=new EmptyVector();
	
	private EmptyVector() {
		
	}

	@Override
	public APersistentVector<T> deleteAt(int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public APersistentVector<T> deleteRange(int start, int end) {
		if ((start==0)&&(end==0)) return this;
		throw new IndexOutOfBoundsException();
	}

	@Override
	public APersistentVector<T> deleteFirst(T value) {
		return this;
	}
	
	@Override
	public APersistentVector<T> excludeAll(Collection<? extends T> values) {
		return this;
	}

	@Override
	public T get(int i) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return -1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListIterator<T> listIterator() {
		return (ListIterator<T>) NullIterator.INSTANCE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListIterator<T> listIterator(int index) {
		return (ListIterator<T>) NullIterator.INSTANCE;
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public APersistentVector<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex!=0)||(toIndex!=0)) throw new IllegalArgumentException();
		return this;
	}

	@Override
	public int compareTo(APersistentVector<T> o) {
		if (o.size()>0) return -1;
		return 0;
	}

	@Override
	public <V> V[] toArray(V[] a, int offset) {
		return null;
	}

	@Override
	public int hashCode() {
		// need to be 0 to be consistent will zero length PersistentList
		return 0;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof List<?>) {
			return equals((List<T>)o);
		}
		return false;
	}
	
	@Override
	public boolean equals(List<T> list) {
		return list.size()==0;
	}

	@Override
	public APersistentVector<T> back() {
		return this;
	}

	@Override
	public APersistentVector<T> front() {
		return this;
	}

	@Override
	public int indexOf(Object value, int start) {
		return -1;
	}

	@Override
	public APersistentVector<T> assocAt(int index, T value) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public APersistentVector<T> insert(int index, T value) {
		if (index!=0) throw new IndexOutOfBoundsException();
		return RepeatVector.create(value,1);
	}

	@Override
	public APersistentVector<T> insertAll(int index, Collection<? extends T> values) {
		if (index!=0) throw new IndexOutOfBoundsException();
		return Vectors.createFromCollection(values);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>) NullIterator.INSTANCE;
	}
	
	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}

	@Override
	public ISeq<T> seq() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public APersistentVector<T> concat(APersistentVector<? extends T> a) {
		return (APersistentVector<T>) a;
	}

	@Override
	public RepeatVector<T> include(T value) {
		return RepeatVector.create(value,1);
	}

}
