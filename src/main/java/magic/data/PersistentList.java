package magic.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import magic.Errors;
import magic.RT;
import magic.data.impl.SubList;

/**
 * Abstract base class for persistent lists
 * @author Mike
 *
 * @param <T>
 */
public abstract class PersistentList<T> extends PersistentCollection<T> implements IPersistentList<T> {
	private static final long serialVersionUID = -7221238938265002290L;

	@Override
	public abstract T get(int i);
	
	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	private class PersistentListIterator implements ListIterator<T> {
		int i;
		
		public PersistentListIterator() {
			i=0;
		}
		
		public PersistentListIterator(int index) {
			i=index;
		}
		
		@Override
		public void add(T e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return (i<size());
		}

		@Override
		public boolean hasPrevious() {
			return i>0;
		}

		@Override
		public T next() {
			return get(i++);
		}

		@Override
		public int nextIndex() {
			int s=size();
			return (i<s)?i+1:s;
		}

		@Override
		public T previous() {
			return get(--i);
		}

		@Override
		public int previousIndex() {
			return i-1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(T e) {
			throw new UnsupportedOperationException();
		}	
	}

	@Override
	public ListIterator<T> listIterator() {
		return new PersistentListIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new PersistentListIterator(index);
	}

	@Override
	public Iterator<T> iterator() {
		return new PersistentListIterator();
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public abstract PersistentList<T> conj(T value);

	@Override
	public PersistentList<T> concat(IPersistentList<T> values) {
		return ListFactory.concat(this,values);
	}
	
	@Override
	public PersistentList<T> concat(Collection<T> values) {
		return ListFactory.concat(this,ListFactory.createFromCollection(values));
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int indexOf(Object o) {
		return indexOf(o,0);
	}
	
	public int indexOf(Object o, int start) {
		int i=start;
		int size=size();
		while(i<size) {
			T it=get(i);
			if (RT.equals(o, it)) return i;
			i++;
		}
		return -1;
	}
	
	@Override
	public PersistentList<T> deleteRange(int start, int end) {
		int size=size();
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException();
		if (start>end) throw new IllegalArgumentException();
		if (start==end) return this;
		if (start==0) return subList(end,size);
		if (end==size) return subList(0,start);
		return subList(0,start).concat(subList(end,size));
	}
	
	@Override
	public T head() {
		return get(0);
	}
	
	@Override
	public PersistentList<T> tail() {
		return subList(1,size());
	}
	
	@Override
	public PersistentList<T> front() {
		int size=size();
		return subList(0,size/2);
	}

	@Override
	public PersistentList<T> back() {
		int size=size();
		return subList(size/2,size);
	}

	@Override
	public PersistentList<T> subList(int fromIndex, int toIndex) {
		// checks that return known lists
		if ((fromIndex==0)&&(toIndex==size())) return this;
		if (fromIndex==toIndex) return ListFactory.emptyList();
		
		// otherwise generate a SubList
		// this also handles exception cases
		return SubList.create(this, fromIndex, toIndex);
	}

	@Override
	public PersistentList<T> update(int index, T value) {
		PersistentList<T> firstPart=subList(0,index);
		PersistentList<T> lastPart=subList(index+1,size());
		return firstPart.conj(value).concat(lastPart);
	}

	@Override
	public PersistentList<T> insert(int index, T value) {
		PersistentList<T> firstPart=subList(0,index);
		PersistentList<T> lastPart=subList(index,size());
		return firstPart.conj(value).concat(lastPart);
	}

	@Override
	public PersistentList<T> insertAll(int index, Collection<T> values) {
		if (values instanceof PersistentList<?>) {
			return insertAll(index,(PersistentList<T>)values);
		}
		PersistentList<T> pl=ListFactory.createFromCollection(values);
		return subList(0,index).concat(pl).concat(subList(index,size()));
	}
	
	@Override
	public PersistentList<T> insertAll(int index, IPersistentList<T> values) {
		PersistentList<T> firstPart=subList(0,index);
		PersistentList<T> lastPart=subList(index,size());
		return firstPart.concat(values).concat(lastPart);
	}
	
	@Override
	public PersistentList<T> delete(T value) {
		PersistentList<T> pl=this;
		for (int i = pl.indexOf(value); i>=0; i=pl.indexOf(value)) {
			pl=pl.subList(0, i).concat(pl.subList(i+1, pl.size()));
		}
		return pl;
	}
	
	@Override
	public PersistentList<T> deleteAt(int index) {
		return deleteRange(index,index+1);
	}

	@Override
	public PersistentList<T> clone() {
		return (PersistentList<T>)super.clone();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof List<?>) {
			return equals((List<T>)o);
		}
		return super.equals(o);
	}
	
	public boolean equals(List<T> pl) {
		if (this==pl) return true;
		int size=size();
		if (size!=pl.size()) return false;
		for (int i=0; i<size; i++) {
			if (!RT.equals(get(i),pl.get(i))) return false;
		}
		return true;
	}
	
	@Override
	public PersistentList<T> copyFrom(int dstIndex, IPersistentList<T> values, int srcIndex, int length) {
		int size=size();
		if ((dstIndex<0)||((dstIndex+length)>size)) throw new IndexOutOfBoundsException();
		if (length<0) throw new IllegalArgumentException(Errors.negativeRange());
		if (length==0) return this;
		return subList(0,dstIndex).concat(values.subList(srcIndex, srcIndex+length)).concat(subList(dstIndex+length,size));
	}

	public static <T> PersistentList<T> coerce(List<T> a) {
		if (a instanceof PersistentList<?>) return (PersistentList<T>) a;
		return ListFactory.createFromList(a);
	}
}
