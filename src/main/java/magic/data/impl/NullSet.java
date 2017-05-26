package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.Iterator;

import magic.data.APersistentSet;
import magic.data.ISeq;
import magic.data.Sets;
import magic.data.Tools;

public final class NullSet<T> extends BasePersistentSet<T> {
	private static final long serialVersionUID = -6170277533575154354L;
	
	@SuppressWarnings("rawtypes")
	public static final NullSet<?> INSTANCE=new NullSet();
	
	private NullSet() {
		
	}
	
	@Override
	public boolean contains(Object t) {
		return false;
	}

	@Override
	public APersistentSet<T> include(T value) {
		return Sets.create(value);
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
	public APersistentSet<T> exclude(final T value) {
		return this;
	}

	@Override
	public APersistentSet<T> excludeAll(APersistentSet<T> values) {
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public APersistentSet<T> includeAll(APersistentSet<? extends T> values) {
		return (APersistentSet<T>) values;
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
