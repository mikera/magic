package magic.data.impl;

import java.util.Collection;

import magic.RT;
import magic.data.IPersistentList;
import magic.data.ISeq;
import magic.data.PersistentList;

@SuppressWarnings("serial")
public abstract class BasePersistentList<T> extends PersistentList<T> {

	public int end() {
		throw new UnsupportedOperationException();
	}
	
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


	public PersistentList<T> deleteFirst(T value) {
		int i=indexOf(value);
		if (i<0) return this;
		return deleteRange(i,i+1);
	}
	
	@Override
	public PersistentList<T> delete(T value) {
		PersistentList<T> pl=this;
		int i=pl.indexOf(value);
		while (i>=0) {
			pl=pl.deleteAt(i);
			i=pl.indexOf(value,i);
		}
		return pl;
	}

	@Override
	public PersistentList<T> deleteAll(Collection<T> values) {
		PersistentList<T> pl=this;
		for (T t : values) { 
			pl=pl.delete(t);
		}
		return pl;
	}

	@Override
	public int compareTo(PersistentList<T> o) {
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
	public PersistentList<T> concat(IPersistentList<T> values) {
		return BlockList.coerce(values).concat(values);
	}
	
	@Override
	public PersistentList<T> conj(T value) {
		return BlockList.coerce(this).conj(value);
	}
	
	@Override
	public ISeq<T> seq() {
		if (size()==0) return null;
		return new ListIndexSeq<T>(this);
	}

}
