package magic.data.impl;

import java.util.List;

import magic.data.IPersistentList;
import magic.data.Lists;
import magic.data.APersistentList;
import magic.data.Tuple;

public class CompositeList<T> extends BasePersistentList<T> {
	private static final long serialVersionUID = 1L;
	
	public final APersistentList<T> front;
	public final APersistentList<T> back;
	private final int size;
	
	public static <T> APersistentList<T> concat(APersistentList<T> a, APersistentList<T> b) {
		int as=a.size(); if (as==0) return b;
		int bs=b.size(); if (bs==0) return a;
		if ((as+bs)<=Lists.MAX_TUPLE_BUILD_SIZE) {
			return Tuple.concat(a, b);
		}
		
		if (a.size()<(b.size()>>1)) {
			return new CompositeList<T>(concat(a,b.front()),b.back());
		}
		
		if (b.size()<(a.size()>>1)) {
			return new CompositeList<T>(a.front(),concat(a.back(),b));
		}
		
		return new CompositeList<T>(a,b);
	}
	
	public static <T> CompositeList<T> create(T[] data,  int fromIndex, int toIndex) {
		int midIndex=calcMidIndex(fromIndex, toIndex);
		return new CompositeList<T>(Lists.createFromArray(data,fromIndex,midIndex),Lists.createFromArray(data,midIndex,toIndex));
	}
	
	public static final int calcMidIndex(int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<0) throw new IllegalArgumentException();
		int splitIndex=n>>1;
		if (splitIndex>Lists.MAX_TUPLE_BUILD_SIZE) {
			// round to a whole number of tuple blocks
			splitIndex=(splitIndex/Lists.MAX_TUPLE_BUILD_SIZE)*Lists.MAX_TUPLE_BUILD_SIZE;
		}
		return fromIndex+splitIndex;
	}
	
	public static <T> CompositeList<T> create(List<T> source) {
		return create(source,0,source.size());
	}
	

	public static <T> CompositeList<T> create(List<T> source, int fromIndex, int toIndex) {
		int midIndex=calcMidIndex(fromIndex, toIndex);
		return new CompositeList<T>(Lists.createFromList(source,fromIndex,midIndex),Lists.createFromList(source,midIndex,toIndex));
	}
	
	private CompositeList(APersistentList<T> a, APersistentList<T> b ) {
		front=a;
		back=b;
		size=a.size()+b.size();
	}
	
	@Override
	public APersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size)) throw new IndexOutOfBoundsException();
		if ((fromIndex==0)&&(toIndex==size)) return this;
		int fs=front.size();
		if (toIndex<=fs) return front.subList(fromIndex, toIndex);
		if (fromIndex>=fs) return back.subList(fromIndex-fs, toIndex-fs);
		return concat(front.subList(fromIndex, fs),back.subList(0, toIndex-fs));
	}
	
	@Override
	public APersistentList<T> front() {
		return front;
	}

	@Override
	public APersistentList<T> back() {
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
	public APersistentList<T> conj(T value) {
		return concat(this,Tuple.of(value));
	}

	@Override
	public APersistentList<T> concat(IPersistentList<T> values) {
		return concat(this,APersistentList.coerce(values));
	}

	@Override
	public int hashCode() {
		int r= Integer.rotateRight(front.hashCode(),back.size());
		r^=back.hashCode();
		return r;
	}
}
