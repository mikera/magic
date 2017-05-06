package magic.data;

import java.util.Collection;
import java.util.ListIterator;

import magic.Errors;
import magic.RT;

/**
 * Abstract base class for persistent lists
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class APersistentList<T> extends APersistentCollection<T> implements IPersistentList<T> {
   
	@Override
	public APersistentList<T> empty() {
		return Lists.emptyList();
	}

	@Override
	public int compareTo(APersistentList<T> o) {
		// TODO: optimise
		return Vectors.coerce(this).compareTo(Vectors.coerce(o));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof APersistentList<?>) {
			return equals((APersistentList<?>)o);
		}
		return false;
	}
	
	public boolean equals(APersistentList<?> a) {
		// TODO: optimise
		return (Vectors.coerce(this)).equals(Vectors.coerce(a));
	}
	
	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException(Errors.immutable(this));
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException(Errors.immutable(this));
	}
	
	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException("No listIterator for you!");
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("No listIterator for you!");
	}
	

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException(Errors.immutable(this));
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException(Errors.immutable(this));
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("(");
		boolean first=true;
		for (T t: this) {
			if (first) {
				first=false;
			} else {
				sb.append(' ');
			}
			sb.append(RT.toString(t));
		}		
		sb.append(')');
		return sb.toString();
	}
}
