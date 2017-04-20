package magic.data;

import java.util.Collection;
import java.util.List;

import magic.RT;
import magic.data.impl.BasePersistentList;
import magic.data.impl.BlockList;
import magic.data.impl.EmptyArrays;

public final class Tuple<T> extends BasePersistentList<T> {
	private static final long serialVersionUID = -3717695950215145009L;

	public final T[] data;
	private final int offset;
	private final int size;
	
	// Empty Tuple for some special cases
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final Tuple<?> EMPTY_TUPLE=new Tuple(EmptyArrays.EMPTY_OBJECTS);
	
	@SuppressWarnings("unchecked")
	public static <T> Tuple<T> of(T... values) {
		int n=values.length;
		if (n==0) return (Tuple<T>) EMPTY_TUPLE;
		T[] ndata=(T[]) new Object[n];
		System.arraycopy(values,0,ndata,0,n);
		return new Tuple<T>(ndata);
	}
	
	public static <T> Tuple<T> wrap(T[] values) {
		return new Tuple<T>(values);
	}
	
	public static <T> Tuple<T> wrap(T[] values,int offset, int size) {
		if ((offset<0)||(offset+size>values.length)||(size<0)) {
			throw new IndexOutOfBoundsException("Invalid index range start="+offset+" size="+size);
		}
		return new Tuple<T>(values,offset,size);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Tuple<T> of(T a) {
		T[] ndata=(T[])new Object[1];
		ndata[0]=a;
		return new Tuple<T>(ndata);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Tuple<T> of(T a, T b) {
		T[] ndata=(T[])new Object[2];
		ndata[0]=a;
		ndata[1]=b;
		return new Tuple<T>(ndata);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Tuple<T> create(T[] values, int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<=0) return (Tuple<T>) EMPTY_TUPLE;
		T[] ndata=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			ndata[i]=values[i+fromIndex];
		}
		return new Tuple<T>(ndata);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Tuple<T> createFrom(Collection<T> values) {
		int n=values.size();
		T[] ndata=(T[]) new Object[n];
		int i=0;
		for (T t : values) {
			ndata[i++]=t;
		}
		return new Tuple<T>(ndata);
	}

	public static <T> Tuple<T> createFrom(List<T> values) {
		return createFrom(values,0,values.size());
	}

	
	@SuppressWarnings("unchecked")
	public static <T> Tuple<T> createFrom(List<T> values, int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<=0) {
			if (n==0) return (Tuple<T>) EMPTY_TUPLE;
			throw new IllegalArgumentException("Negative range in Tuple.create: ("+fromIndex+","+toIndex+")");
		}
		T[] ndata=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			ndata[i]=values.get(i+fromIndex);
		}
		return new Tuple<T>(ndata);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentList<T> concat(List<T> a, List<T> b) {
		int aSize=a.size();
		int bSize=b.size();
		int newSize=aSize+bSize;
		if (newSize>2*BlockList.BASE_BLOCKSIZE) {
			return BlockList.create(a).concat(b);
		}
		T[] ndata=(T[]) new Object[newSize];
		for (int i=0; i<aSize; i++) {
			ndata[i]=a.get(i);
		}
		for (int i=0; i<bSize; i++) {
			ndata[aSize+i]=b.get(i);
		}

		return new Tuple<T>(ndata);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	private Tuple(T[] values) {
		this(values,0,values.length);
	}
	
	private Tuple(T[] values, int offset, int size) {
		data=values;
		this.offset=offset;
		this.size=size;
	}
	
	@Override
	public T get(int i) {
		if ((i<0)||(i>=size)) throw new IndexOutOfBoundsException("Index: "+i);
		return data[i+offset];
	}
	
	@Override
	public Tuple<T> clone() {
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Tuple<T> subList(int fromIndex, int toIndex) {
		int newSize=toIndex-fromIndex;
		if ((fromIndex<0)||(toIndex>size)) throw new IndexOutOfBoundsException();
		if ((fromIndex==0)&&(toIndex==size)) return this;
		if (newSize==0) return (Tuple<T>) Tuple.EMPTY_TUPLE;
		if (newSize<0)throw new IllegalArgumentException("Negative range specified");

		return Tuple.wrap(data, offset+fromIndex, newSize);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public PersistentList<T> deleteRange(int start, int end) {
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException();
		if (start>end) throw new IllegalArgumentException("Negative range specified");
		if ((start==0)&&(end==size)) return (PersistentList<T>) EMPTY_TUPLE;
		if (start==end) return this;
		int newSize=size-(end-start);
		T[] ndata=(T[]) new Object[newSize];
		if (start>0) System.arraycopy(data, offset, ndata, 0, start);
		if (end<size) System.arraycopy(data, offset+end, ndata, start, size-end);
		return new Tuple<T>(ndata,0,newSize);
	}

	@Override
	public ISeq<T> seq() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PersistentList<T> conj(T value) {
		if (size<BlockList.BASE_BLOCKSIZE) {
			int newSize=size+1;
			T[] ndata=(T[]) new Object[newSize];
			System.arraycopy(data, offset, ndata, 0, size);
			ndata[size]=value;
			return Tuple.wrap(ndata,0,newSize);
		} else {
			return BlockList.coerce(this).conj(value);
		}
	}
	
	@Override
	public int hashCode() {
		return RT.arrayHashCode(data, offset, size);
	}

}
