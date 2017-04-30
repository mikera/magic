package magic.data.impl;

import magic.data.APersistentVector;
import magic.data.IPersistentVector;
import magic.data.ISeq;
import magic.data.PersistentVector;

@SuppressWarnings("serial")
public abstract class BasePersistentVector<T> extends APersistentVector<T> implements IPersistentVector<T> {

	@Override
	public boolean contains(Object o) {
		return indexOf(o)>=0;
	}

	@Override
	public int lastIndexOf(Object o) {
		int i=0;
		int res=-1;
		for (T it: this) {
			if (it!=null) {
				if (it.equals(o)) res=i;
			} else {
				if (o==null) res=i;
			}
			i++;
		}
		return res;
	}
	
	/**
	 * Returns hashcode of the persistent array. Defined as XOR of hashcodes of all elements rotated right for each element
	 */
	@Override
	public int hashCode() {
		int result=0;
		for (int i=0; i<size(); i++) {
			Object v=get(i);
			if (v!=null) {
				result^=v.hashCode();
			}
			result=Integer.rotateRight(result, 1);
		}
		return result;
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
	public APersistentVector<T> concat(APersistentVector<T> values) {
		return PersistentVector.coerce(this).concat(values);
	}
	
	@Override
	public APersistentVector<T> include(T value) {
		return PersistentVector.coerce(this).include(value);
	}
	
	@Override
	public ISeq<T> seq() {
		if (size()==0) return null;
		return new ListIndexSeq<T>(this);
	}

}
