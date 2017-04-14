package magic.data;

import java.util.Collection;
import java.util.List;

import magic.data.impl.BasePersistentList;
import magic.data.impl.BlockList;
import magic.data.impl.EmptyArrays;
import magic.data.impl.SingletonList;
import magic.data.impl.SubTuple;

public final class Tuple<T> extends BasePersistentList<T> {
	private static final long serialVersionUID = -3717695950215145009L;

	public final T[] data;
	
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
	public static <T> Tuple<T> concat(List<T> a, List<T> b) {
		int as=a.size();
		int bs=b.size();
		T[] ndata=(T[]) new Object[as+bs];
		for (int i=0; i<as; i++) {
			ndata[i]=a.get(i);
		}
		for (int i=0; i<bs; i++) {
			ndata[as+i]=b.get(i);
		}

		return new Tuple<T>(ndata);
	}
	
	@Override
	public int size() {
		return data.length;
	}
	
	private Tuple(T[] values) {
		data=values;
	}
	
	@Override
	public T get(int i) {
		return data[i];
	}
	
	@Override
	public Tuple<T> clone() {
		return this;
	}
	
	@Override
	public PersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size())) throw new IndexOutOfBoundsException();
		if ((fromIndex==0)&&(toIndex==size())) return this;
		if (fromIndex>=toIndex) {
			if (fromIndex==toIndex) return ListFactory.emptyList();
			throw new IllegalArgumentException();
		}
		if (fromIndex+1==toIndex) {
			return SingletonList.of(data[fromIndex]);
		}
		return SubTuple.create(data, fromIndex, toIndex-fromIndex);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public PersistentList<T> deleteRange(int start, int end) {
		if ((start<0)||(end>size())) throw new IndexOutOfBoundsException();
		if (start>=end) {
			if (start>end) throw new IllegalArgumentException();
			return this;
		}
		if ((start==0)&&(end==size())) return ListFactory.emptyList();
		if (start==end) return this;
		int ns=size()-(end-start);
		T[] ndata=(T[]) new Object[ns];
		System.arraycopy(data, 0, ndata, 0, start);
		System.arraycopy(data, end, ndata, start, size()-end);
		return new Tuple<T>(ndata);
	}

	@Override
	public ISeq<T> seq() {
		return null;
	}

	@Override
	public PersistentList<T> conj(T value) {
		return BlockList.coerce(this).conj(value);
	}

}
