package magic.data.impl;

import magic.data.ListFactory;
import magic.data.PersistentList;

/**
 * Implements a persistent list that is a subset of an existing tuple
 * utilising the same immutable backing array
 * 
 * @author Mike
 *
 * @param <T>
 */
public final class SubTuple<T> extends BasePersistentList<T>   {	

	private static final long serialVersionUID = 3559316900529560364L;

	private final T[] data;
	private final int offset;
	private final int length;
	
	public static <T> SubTuple<T> create(T[] valuesDirect, int offset, int length) {
		return new SubTuple<T>(valuesDirect,offset,length);
	}
	
	@Override
	public int size() {
		return length;
	}
	
	private SubTuple(T[] valuesDirect, int off, int len) {
		data=valuesDirect;
		offset=off;
		length=len;	
	}
	
	@Override
	public T get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException();
		return data[i+offset];
	}
	
	@Override
	public SubTuple<T> clone() {
		return this;
	}
	
	@Override
	public PersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size())) throw new IndexOutOfBoundsException();
		if (fromIndex>=toIndex) {
			if (fromIndex>toIndex) throw new IllegalArgumentException();
			return ListFactory.emptyList();
		}
		if ((fromIndex==0)&&(toIndex==size())) return this;
		return SubTuple.create(data, offset+fromIndex, toIndex-fromIndex);
	}
}
