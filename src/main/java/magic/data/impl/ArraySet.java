package magic.data.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import magic.RT;
import magic.data.ISeq;
import magic.data.PersistentSet;

/**
 * Array based immutable set implementation
 * 
 * Not great performance - since set membership testing requires
 * full scan of array. Hence should only be used for small sets.
 * 
 * 
 * @author Mike
 *
 * @param <T>
 */
public final class ArraySet<T> extends PersistentSet<T> {
	private final T[] data;
	
	@SuppressWarnings("unchecked")
	public static <T> ArraySet<T> createFromSet(Set<T> source) {
		return new ArraySet<T>((T[])source.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArraySet<T> createFromSet(PersistentSet<T> source) {
		if (source instanceof ArraySet<?>) return (ArraySet<T>)source;
		return new ArraySet<T>((T[])source.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArraySet<T> createFromValue(T source) {
		return new ArraySet<T>((T[])new Object[]{source});
	}
	
	public static <T> ArraySet<T> createFromArray(T[] source) {
		HashSet<T> hs=new HashSet<T>();
		for (int i=0; i<source.length; i++) {
			hs.add(source[i]);
		}
		return createFromSet(hs);
	}
	
	@Override public boolean contains(Object o) {
		for (T t : data) {
			if (RT.equals(t, o)) return true;
		}
		return false;
	}
	
	private ArraySet(T[] newData) {
		data=newData;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new ArraySetIterator<T>();
	}

	@Override
	public int size() {
		return data.length;
	}
	
	private int indexOf(T value) {
		for (int i=0; i<data.length; i++) {
			if (RT.equals(value,data[i])) return i;
		}
		return -1;
	}
	
	private class ArraySetIterator<K> implements Iterator<K> {
		private int pos=0;
		
		@Override
		public boolean hasNext() {
			return pos<data.length;
		}

		@Override
		@SuppressWarnings("unchecked")
		public K next() {
			if (pos>=data.length) throw new NoSuchElementException();
			return (K)data[pos++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final long serialVersionUID = -3125683703717134995L;

	@SuppressWarnings("unchecked")
	@Override
	public PersistentSet<T> conj(T value) {
		if (contains(value)) return this;
		
		T[] ndata=(T[])new Object[data.length+1];
		System.arraycopy(data, 0, ndata, 0, data.length);
		ndata[data.length]=value;
		return new ArraySet<T>(ndata);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PersistentSet<T> delete(T value) {
		int pos=indexOf(value);
		if (pos<0) return this;
		int size=data.length;
		if (size<=2) {
			if (size==2) return SingletonSet.create(data[1-pos]);
			return (PersistentSet<T>) NullSet.INSTANCE;
		}
		T[] ndata=(T[])new Object[size-1];
		System.arraycopy(data, 0, ndata, 0, pos);
		System.arraycopy(data, pos+1, ndata, pos, size-pos-1);
		return createFromArray(ndata);
	}

	@Override
	public ISeq<T> seq() {
		// TODO Auto-generated method stub
		return null;
	}

}
