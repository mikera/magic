package magic.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import magic.Errors;
import magic.RT;
import magic.data.impl.ListIndexSeq;
import magic.data.impl.SubVector;

/**
 * Abstract base class for persistent lists
 * @author Mike
 *
 * @param <T>
 */
public abstract class APersistentVector<T> extends APersistentCollection<T> implements IPersistentVector<T> {
	private static final long serialVersionUID = -7221238938265002290L;

	@Override
	public abstract T get(int i);
	
	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException(Errors.immutable(this));
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException(Errors.immutable(this));
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
	public APersistentVector<T> exclude(T value) {
		APersistentVector<T> pl=this;
		int i=pl.indexOf(value);
		while (i>=0) {
			pl=pl.deleteAt(i);
			i=pl.indexOf(value,i);
		}
		return pl;
	}

	@Override
	public APersistentVector<T> excludeAll(Collection<T> values) {
		APersistentVector<T> pl=this;
		for (T t : values) { 
			pl=pl.exclude(t);
		}
		return pl;
	} 



	@Override 
	public final APersistentVector<T> concat(IPersistentCollection<T> values) {
		return concat(Vectors.coerce(values));
	}

	public APersistentVector<T> concat(APersistentVector<T> values) {
		return PersistentVector.coerce(this).concat(values);
	}
	
	@Override
	public abstract APersistentVector<T> include(T value);
	
	@Override
	public APersistentVector<T> concat(Collection<T> values) {
		return Vectors.concat(this,Vectors.createFromCollection(values));
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ISeq<T> seq() {
		if (size()==0) return null;
		return new ListIndexSeq<T>(this);
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
	public boolean contains(Object o) {
		return indexOf(o)>=0;
	}
	
	/**
	 * Returns hashcode of the persistent array. Defined as XOR of hashcodes of all elements rotated right for each element
	 */
	@Override
	public int hashCode() {
		int result=0;
		for (int i=0; i<size(); i++) {
			Object v=get(i);
			result^=RT.hashCode(v);
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}

	
	@Override
	public int lastIndexOf(Object o) {
		int size=size();
		int i=size-1;
		while(i>=0) {
			T it=get(i);
			if (RT.equals(o, it)) return i;
			i--;
		}
		return -1;
	}
	
	@Override
	public APersistentVector<T> deleteRange(int start, int end) {
		int size=size();
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException();
		if (start>end) throw new IllegalArgumentException();
		if (start==end) return this;
		if (start==0) return subList(end,size);
		if (end==size) return subList(0,start);
		return subList(0,start).concat(subList(end,size));
	}
	
	/**
	 * Deletes the first instance of a specified value in the collection"
	 */
	public APersistentVector<T> deleteFirst(T value) {
		int i=indexOf(value);
		if (i<0) return this;
		return deleteRange(i,i+1);
	}
	
	@Override
	public T head() {
		return get(0);
	}
	
	@Override
	public APersistentVector<T> tail() {
		return subList(1,size());
	}
	
	@Override
	public APersistentVector<T> front() {
		int size=size();
		return subList(0,size/2);
	}

	@Override
	public APersistentVector<T> back() {
		int size=size();
		return subList(size/2,size);
	}

	@Override
	public APersistentVector<T> subList(int fromIndex, int toIndex) {
		// checks that return known lists
		if ((fromIndex==0)&&(toIndex==size())) return this;
		if (fromIndex==toIndex) return Vectors.emptyVector();
		
		// otherwise generate a SubList
		// this also handles exception cases
		return SubVector.create(this, fromIndex, toIndex);
	}

	@Override
	public APersistentVector<T> update(int index, T value) {
		APersistentVector<T> firstPart=subList(0,index);
		APersistentVector<T> lastPart=subList(index+1,size());
		return firstPart.include(value).concat(lastPart);
	}

	@Override
	public APersistentVector<T> insert(int index, T value) {
		APersistentVector<T> firstPart=subList(0,index);
		APersistentVector<T> lastPart=subList(index,size());
		return firstPart.include(value).concat(lastPart);
	}

	public final APersistentVector<T> insertAll(int index, IPersistentCollection<T> values) {
		return insertAll(index,Vectors.coerce(values));
	}
	
	@Override
	public APersistentVector<T> insertAll(int index, Collection<T> values) {
		return insertAll(index,Vectors.coerce(values));
	}
	
	@Override
	public APersistentVector<T> insertAll(int index, APersistentVector<T> values) {
		APersistentVector<T> firstPart=subList(0,index);
		APersistentVector<T> lastPart=subList(index,size());
		return firstPart.concat(values).concat(lastPart);
	}
	
	@Override
	public APersistentVector<T> deleteAt(int index) {
		return deleteRange(index,index+1);
	}

	@Override
	public APersistentVector<T> clone() {
		return (APersistentVector<T>)super.clone();
	}
	
	@Override
	public APersistentVector<T> empty() {
		return Vectors.emptyVector();
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
	
	public boolean equals(APersistentVector<T> pl) {
		if (this==pl) return true;
		int size=size();
		if (size!=pl.size()) return false;
		for (int i=0; i<size; i++) {
			if (!RT.equals(get(i),pl.get(i))) return false;
		}
		return true;
	}
	
	@Override
	public final APersistentVector<T> copyFrom(int dstIndex, IPersistentCollection<T> values, int srcIndex, int length) {
		return copyFrom(dstIndex,Vectors.coerce(values),srcIndex,length);
	}
	
	public APersistentVector<T> copyFrom(int dstIndex, APersistentVector<T> values, int srcIndex, int length) {
		int size=size();
		if ((dstIndex<0)||((dstIndex+length)>size)) throw new IndexOutOfBoundsException();
		if (length<0) throw new IllegalArgumentException(Errors.negativeRange());
		if (length==0) return this;
		return subList(0,dstIndex).concat(values.subList(srcIndex, srcIndex+length)).concat(subList(dstIndex+length,size));
	}

	public static <T> APersistentVector<T> coerce(List<T> a) {
		if (a instanceof APersistentVector<?>) return (APersistentVector<T>) a;
		return Vectors.createFromList(a);
	}
	
	public int compareTo(APersistentVector<T> o) {
		int n=magic.Maths.min(o.size(), size());
		for (int i=0; i<n; i++) {
			int r=RT.compare(this.get(i), o.get(i));
			if (r!=0) return r;
		}
		if (size()<o.size()) return -1;
		if (size()>o.size()) return 1;
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("[");
		boolean first=true;
		for (T t: this) {
			if (first) {
				first=false;
			} else {
				sb.append(' ');
			}
			sb.append(RT.toString(t));
		}		
		sb.append(']');
		return sb.toString();
	}
}
