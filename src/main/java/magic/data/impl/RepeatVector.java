package magic.data.impl;

import magic.Errors;
import magic.RT;
import magic.data.APersistentVector;
import magic.data.Tuple;
import magic.data.Vectors;

/**
 * Persistent list that implements a repeating single value
 * 
 * @author Mike
 *
 * @param <T>
 */
public class RepeatVector<T> extends APersistentVector<T> {
	private static final long serialVersionUID = -4991558599811750311L;

	private final T value;
	private final int size;
	
	private RepeatVector(T object, int num) {
		value=object;
		size=num;
	}
	
	public static <T> RepeatVector<T> create(T object, int number) {
		return new RepeatVector<T>(object,number);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public T get(int i) {
		if ((i<0)||(i>=size)) throw new IndexOutOfBoundsException(Errors.indexOutOfBounds(i));
		return value;
	}
	
	@Override
	public APersistentVector<T> subList(int start, int end) {
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException(Errors.rangeOutOfBounds(start,end));
		if (start==end) return Vectors.emptyVector();
		int num=end-start;
		if (num<0) {
			throw new IllegalArgumentException(Errors.negativeRange());
		}
		if (num==size) return this;
		return create(value,num);
	}
	
	@Override
	public APersistentVector<T> deleteRange(int start, int end) {
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException(Errors.rangeOutOfBounds(start,end));
		if (start==end) return this;
		int numDeleted=end-start;
		if (numDeleted<0) {
			throw new IllegalArgumentException(Errors.negativeRange());
		}
		if (numDeleted==size) return Vectors.emptyVector();
		if (numDeleted==0) return this;
		return create(value,size-numDeleted);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public APersistentVector<T> concat(APersistentVector<? extends T> values) {
		if (values instanceof RepeatVector<?>) {
			RepeatVector<T> ra=(RepeatVector<T>)values;
			if (RT.equals(ra.value, value)) {
				return create(value,ra.size+size);
			}
		}
		return super.concat(values);
	}
	
	@Override
	public APersistentVector<T> include(T value) {
		if ((size==0)||(value==this.value)) {
			return create(value,size+1);
		}
		return Tuple.coerce(this).include(value);
	}
	
	@Override
	public APersistentVector<T> exclude(final T v) {
		if (RT.equals(v,value)) {
			return Vectors.emptyVector();
		}
		return this;
	}
}
