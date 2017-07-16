package magic.data.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import magic.RT;
import magic.data.PersistentHashSet;
import magic.data.APersistentSet;

/**
 * Singleton set instance
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public final class SingletonSet<T> extends BasePersistentSet<T> {
	private static final long serialVersionUID = -8579831785484446664L;

	final T value;
	
	public static <T> SingletonSet<T> create(T object) {
		return new SingletonSet<T>(object);
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	private SingletonSet(T object) {
		value=object;
	}

	@Override
	public APersistentSet<T> include(T value) {
		if (value==this.value) return this;
		return PersistentHashSet.coerce(this).include(value);
	}
	
	@Override
	public boolean contains(Object o) {
		return RT.equals(value, o);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int pos=0;
			@Override
			public boolean hasNext() {
				return (pos<1);
			}

			@Override
			public T next() {
				if (pos>0) throw new NoSuchElementException();
				pos++;
				return value;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
