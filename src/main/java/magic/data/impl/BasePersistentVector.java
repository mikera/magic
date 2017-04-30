package magic.data.impl;

import java.util.Collection;

import magic.RT;
import magic.data.IPersistentList;
import magic.data.ISeq;
import magic.data.PersistentVector;
import magic.data.APersistentList;

@SuppressWarnings("serial")
public abstract class BasePersistentVector<T> extends APersistentList<T> {

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
	public APersistentList<T> deleteFirst(T value) {
		int i=indexOf(value);
		if (i<0) return this;
		return deleteRange(i,i+1);
	}
	
	/**
	 * Deletes all instances of a specified value in the collection"
	 */
	@Override
	public APersistentList<T> exclude(T value) {
		APersistentList<T> pl=this;
		int i=pl.indexOf(value);
		while (i>=0) {
			pl=pl.deleteAt(i);
			i=pl.indexOf(value,i);
		}
		return pl;
	}

	@Override
	public APersistentList<T> excludeAll(Collection<T> values) {
		APersistentList<T> pl=this;
		for (T t : values) { 
			pl=pl.exclude(t);
		}
		return pl;
	}

	@Override
	public int compareTo(APersistentList<T> o) {
		int n=magic.Maths.min(o.size(), size());
		for (int i=0; i<n; i++) {
			int r=RT.compare(this, o);
			if (r!=0) return r;
		}
		if (size()<o.size()) return -1;
		if (size()>o.size()) return 1;
		return 0;
	}

	@Override
	public APersistentList<T> concat(IPersistentList<T> values) {
		return PersistentVector.coerce(this).concat(values);
	}
	
	@Override
	public APersistentList<T> include(T value) {
		return PersistentVector.coerce(this).include(value);
	}
	
	@Override
	public ISeq<T> seq() {
		if (size()==0) return null;
		return new ListIndexSeq<T>(this);
	}

}
