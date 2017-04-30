package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class NullIterator<T> implements ListIterator<T> {
	
	@SuppressWarnings({ "rawtypes" })
	public static NullIterator<?> INSTANCE= new NullIterator();
	
	private NullIterator() {
		
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPrevious() {
		return false;
	}

	@Override
	public int nextIndex() {
		return 0;
	}

	@Override
	public T previous() {
		throw new NoSuchElementException();
	}

	@Override
	public int previousIndex() {
		return -1;
	}

	@Override
	public void set(T e) {
		throw new UnsupportedOperationException();
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}
}
