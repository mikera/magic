package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;

import magic.data.ISeq;
import magic.data.PersistentSet;
import magic.data.SetFactory;
import magic.data.Tools;

public final class NullSet<T> extends BasePersistentSet<T> {
	private static final long serialVersionUID = -6170277533575154354L;
	
	@SuppressWarnings("rawtypes")
	public static NullSet<?> INSTANCE=new NullSet();
	
	private NullSet() {
		
	}
	
	@Override
	public boolean contains(Object t) {
		return false;
	}

	@Override
	public PersistentSet<T> conj(T value) {
		return SetFactory.create(value);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>) NullIterator.INSTANCE;
	}
	
	@Override
	public PersistentSet<T> delete(final T value) {
		return this;
	}

	@Override
	public PersistentSet<T> deleteAll(final Collection<T> values) {
		return this;
	}
	
	@Override
	public PersistentSet<T> includeAll(final Collection<T> values) {
		return SetFactory.createFrom(values);
	}
	
	public PersistentSet<T> include(final PersistentSet<T> values) {
		return SetFactory.create(values);
	}
	
	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public ISeq<T> seq() {
		return Tools.seq(this.iterator());
	}
}
