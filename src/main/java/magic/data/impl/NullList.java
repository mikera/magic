package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import magic.data.IPersistentList;
import magic.data.ISeq;
import magic.data.Lists;
import magic.data.APersistentList;

public final class NullList<T> extends BasePersistentList<T> {
	
	private static final long serialVersionUID = -268387358134950528L;

	@SuppressWarnings("rawtypes")
	public static NullList<?> INSTANCE=new NullList();
	
	private NullList() {
		
	}

	@Override
	public APersistentList<T> deleteAt(int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public APersistentList<T> deleteRange(int start, int end) {
		if ((start==0)&&(end==0)) return this;
		throw new IndexOutOfBoundsException();
	}

	@Override
	public APersistentList<T> deleteFirst(T value) {
		return this;
	}
	
	@Override
	public APersistentList<T> excludeAll(Collection<T> values) {
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
	public APersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex!=0)||(toIndex!=0)) throw new IllegalArgumentException();
		return this;
	}

	@Override
	public int compareTo(APersistentList<T> o) {
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
	public APersistentList<T> back() {
		return this;
	}

	@Override
	public APersistentList<T> front() {
		return this;
	}

	@Override
	public int indexOf(Object value, int start) {
		return -1;
	}

	@Override
	public APersistentList<T> update(int index, T value) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public APersistentList<T> insert(int index, T value) {
		if (index!=0) throw new IndexOutOfBoundsException();
		return SingletonList.of(value);
	}

	@Override
	public APersistentList<T> insertAll(int index, Collection<T> values) {
		if (index!=0) throw new IndexOutOfBoundsException();
		return Lists.createFromCollection(values);
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
	public APersistentList<T> copyFrom(int index, IPersistentList<T> values,
			int srcIndex, int length) {
		if (length>0) throw new IndexOutOfBoundsException();
		return this;
	}

	@Override
	public ISeq<T> seq() {
		return null;
	}

	@Override
	public APersistentList<T> concat(IPersistentList<T> a) {
		return APersistentList.coerce(a);
	}

	@Override
	public SingletonList<T> include(T value) {
		return SingletonList.of(value);
	}

}
