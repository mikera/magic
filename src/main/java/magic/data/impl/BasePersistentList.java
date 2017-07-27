package magic.data.impl;

import java.util.Collection;
import java.util.Iterator;

import magic.RT;
import magic.data.APersistentList;
import magic.data.IPersistentList;
import magic.data.ISeq;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.PersistentVector;
import magic.data.Tuple;

/**
 * Base class for persistent lists containing default implementations
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class BasePersistentList<T> extends APersistentList<T> {

	@Override
	public APersistentList<T> concat(IPersistentList<? extends T> values) {
		return PersistentList.coerce(this).concat(values);
	}

	@Override
	public APersistentList<T> concat(Collection<? extends T> values) {
		return PersistentList.coerce(this).concat(values);
	}

	@Override
	public APersistentList<T> concat(APersistentList<? extends T> values) {
		return PersistentList.coerce(this).concat(values);
	}

	@Override
	public APersistentList<T> insert(int index, T value) {
		return PersistentList.coerce(this).insert(index,value);
	}

	@Override
	public APersistentList<T> insertAll(int index, Collection<? extends T> values) {
		return PersistentList.coerce(this).insertAll(index,values);
	}

	@Override
	public APersistentList<T> insertAll(int index, IPersistentList<? extends T> values) {
		return PersistentList.coerce(this).insertAll(index,values);
	}

	@Override
	public APersistentList<T> copyFrom(int index, IPersistentList<? extends T> values, int srcIndex, int length) {
		return PersistentList.coerce(this).copyFrom(index,values,srcIndex,length);
	}

	@Override
	public APersistentList<T> include(T value) {
		return PersistentList.coerce(this).include(value);
	}

	@Override
	public APersistentList<T> deleteAt(int index) {
		return PersistentList.coerce(this).deleteAt(index);
	}

	@Override
	public APersistentList<T> deleteRange(int startIndex, int endIndex) {
		return PersistentList.coerce(this).deleteRange(startIndex,endIndex);
	}

	@Override
	public APersistentList<T> update(int index, T value) {
		return PersistentList.coerce(this).update(index,value);
	}

	@Override
	public T head() {
		return get(0);
	}

	@Override
	public APersistentList<T> tail() {
		return PersistentList.coerce(this).tail();
	}

	@Override
	public APersistentList<T> front() {
		return PersistentList.coerce(this).front();
	}

	@Override
	public APersistentList<T> back() {
		return PersistentList.coerce(this).back();
	}

	@Override
	public ISeq<T> seq() {
		return Tuple.createFrom(this).seq();
	}

	@Override
	public Iterator<T> iterator() {
		return Tuple.createFrom(this).iterator();
	}

	@Override
	public int indexOf(Object o) {
		int i=0;
		for (T t:this) {
			if (RT.equals(o, t)) return i;
			i++;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return Tuple.createFrom(this).lastIndexOf(o);
	}

	@Override
	public APersistentList<T> assocAt(int key, Object value) {
		return PersistentList.coerce(this).assocAt(key, value);
	}

	@Override
	public APersistentList<T> subList(int fromIndex, int toIndex) {
		return Lists.coerce(PersistentVector.coerce(this).subList(fromIndex, toIndex));
	}

	@Override
	public abstract T get(int i);

	@Override
	public abstract int size();

}
