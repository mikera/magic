package magic.data.impl;

import magic.Errors;
import magic.RT;
import magic.data.IPersistentList;
import magic.data.Lists;
import magic.data.APersistentList;

/**
 * Persistent list that implements a repeating single value
 * 
 * @author Mike
 *
 * @param <T>
 */
public class RepeatList<T> extends BasePersistentVector<T> {
	private static final long serialVersionUID = -4991558599811750311L;

	private final T value;
	private final int size;
	
	private RepeatList(T object, int num) {
		value=object;
		size=num;
	}
	
	public static <T> RepeatList<T> create(T object, int number) {
		return new RepeatList<T>(object,number);
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
	public APersistentList<T> subList(int start, int end) {
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException(Errors.rangeOutOfBounds(start,end));
		if (start==end) return Lists.emptyList();
		int num=end-start;
		if (num<0) {
			throw new IllegalArgumentException(Errors.negativeRange());
		}
		if (num==size) return this;
		return create(value,num);
	}
	
	@Override
	public APersistentList<T> deleteRange(int start, int end) {
		if ((start<0)||(end>size)) throw new IndexOutOfBoundsException(Errors.rangeOutOfBounds(start,end));
		if (start==end) return this;
		int numDeleted=end-start;
		if (numDeleted<0) {
			throw new IllegalArgumentException(Errors.negativeRange());
		}
		if (numDeleted==size) return Lists.emptyList();
		if (numDeleted==0) return this;
		return create(value,size-numDeleted);
	}
	
	@Override
	public APersistentList<T> concat(IPersistentList<T> values) {
		if (values instanceof RepeatList<?>) {
			RepeatList<T> ra=(RepeatList<T>)values;
			if (RT.equals(ra.value, value)) {
				return create(value,ra.size+size);
			}
		}
		return super.concat(values);
	}
	
	@Override
	public APersistentList<T> exclude(final T v) {
		if (RT.equals(v,value)) {
			return Lists.emptyList();
		}
		return this;
	}
}
