package magic.data.impl;

import java.util.List;

import magic.data.Vectors;
import magic.data.APersistentVector;
import magic.data.Tuple;

public class CompositeVector<T> extends APersistentVector<T> {
	private static final long serialVersionUID = 1L;
	
	public final APersistentVector<T> front;
	public final APersistentVector<T> back;
	private final int size;
	
	public static <T> APersistentVector<T> concat(APersistentVector<T> a, APersistentVector<T> b) {
		int as=a.size(); if (as==0) return b;
		int bs=b.size(); if (bs==0) return a;
		if ((as+bs)<=Vectors.MAX_TUPLE_BUILD_SIZE) {
			return Tuple.concat(a, b);
		}
		
		if (a.size()<(b.size()>>1)) {
			return new CompositeVector<T>(concat(a,b.front()),b.back());
		}
		
		if (b.size()<(a.size()>>1)) {
			return new CompositeVector<T>(a.front(),concat(a.back(),b));
		}
		
		return new CompositeVector<T>(a,b);
	}
	
	public static <T> CompositeVector<T> create(T[] data,  int fromIndex, int toIndex) {
		int midIndex=calcMidIndex(fromIndex, toIndex);
		return new CompositeVector<T>(Vectors.createFromArray(data,fromIndex,midIndex),Vectors.createFromArray(data,midIndex,toIndex));
	}
	
	public static final int calcMidIndex(int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<0) throw new IllegalArgumentException();
		int splitIndex=n>>1;
		if (splitIndex>Vectors.MAX_TUPLE_BUILD_SIZE) {
			// round to a whole number of tuple blocks
			splitIndex=(splitIndex/Vectors.MAX_TUPLE_BUILD_SIZE)*Vectors.MAX_TUPLE_BUILD_SIZE;
		}
		return fromIndex+splitIndex;
	}
	
	public static <T> CompositeVector<T> create(List<T> source) {
		return create(source,0,source.size());
	}
	

	public static <T> CompositeVector<T> create(List<T> source, int fromIndex, int toIndex) {
		int midIndex=calcMidIndex(fromIndex, toIndex);
		return new CompositeVector<T>(Vectors.createFromList(source,fromIndex,midIndex),Vectors.createFromList(source,midIndex,toIndex));
	}
	
	private CompositeVector(APersistentVector<T> a, APersistentVector<T> b ) {
		front=a;
		back=b;
		size=a.size()+b.size();
	}
	
	@Override
	public APersistentVector<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size)) throw new IndexOutOfBoundsException();
		if ((fromIndex==0)&&(toIndex==size)) return this;
		int fs=front.size();
		if (toIndex<=fs) return front.subList(fromIndex, toIndex);
		if (fromIndex>=fs) return back.subList(fromIndex-fs, toIndex-fs);
		return concat(front.subList(fromIndex, fs),back.subList(0, toIndex-fs));
	}
	
	@Override
	public APersistentVector<T> front() {
		return front;
	}

	@Override
	public APersistentVector<T> back() {
		return back;
	}


	@Override
	public T get(int i) {
		int fs=front.size();
		if (i<fs) {
			return front.get(i);
		}
		return back.get(i-fs);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public APersistentVector<T> include(T value) {
		return concat(this,Tuple.of(value));
	}

	@Override
	public APersistentVector<T> concat(APersistentVector<T> values) {
		return concat(this,Vectors.coerce(values));
	}

	@Override
	public int hashCode() {
		int r= Integer.rotateRight(front.hashCode(),back.size());
		r^=back.hashCode();
		return r;
	}
}
