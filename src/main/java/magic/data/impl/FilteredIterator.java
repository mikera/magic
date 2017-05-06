package magic.data.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implements an iterator that filters on a given predicate.
 * 
 * Concrete implementations should override the abstract 
 * method filter()
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public abstract class FilteredIterator<T> implements Iterator<T> {
	private Iterator<T> base; // belonging to us
	private T next;
	private boolean havenext=false;
	
	public FilteredIterator(Iterator<T> baseIterator) {
		base=baseIterator;
		havenext=findNext();
	}
	
	public abstract boolean filter(T value);
	
	@Override
	public boolean hasNext() {
		return havenext;
	}
	
	private boolean findNext() {
		while (base.hasNext()) {
			T t = base.next();
			if (filter(t)) {
				next=t;
				havenext=true;
				return true;
			}
		}
		return false;
	}

	@Override
	public T next() {
		if (havenext) {			
			T result=next;
			havenext=findNext();
			return result;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
