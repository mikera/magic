package magic.data.impl;

import magic.data.Vectors;
import magic.data.APersistentVector;
import magic.data.Tuple;

/**
 * Singleton list instance
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public final class SingletonList<T> extends BasePersistentVector<T> {

	private static final long serialVersionUID = 8273587747838774580L;
	
	final T value;
	
	public static <T> SingletonList<T> of(T object) {
		return new SingletonList<T>(object);
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public T get(int i) {
		if (i==0) return value;
		throw new IndexOutOfBoundsException();
	}
	
	private SingletonList(T object) {
		value=object;
	}
	
	@Override
	public APersistentVector<T> front() {
		return this;
	}
	
	@Override
	public T head() {
		return value;
	}

	@Override
	public APersistentVector<T> back() {
		return Vectors.emptyList();
	}
	
	@Override
	public APersistentVector<T> tail() {
		return Vectors.emptyList();
	}

	@Override
	public APersistentVector<T> include(T value) {
		return Tuple.of(this.value,value);
	}
}
