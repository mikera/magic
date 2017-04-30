package magic.data;

import java.io.Serializable;
import java.util.Collection;

public interface IPersistentCollection<T> extends Collection<T>, ISeqable<T>, Cloneable, Serializable {

	// include methods
	
	public APersistentCollection<T> includeAll(final Collection<T> values);

	public APersistentCollection<T> includeAll(final IPersistentCollection<T> values);

	// delete methods
	
	public APersistentCollection<T> delete(final T value);
	
	public APersistentCollection<T> deleteAll(final Collection<T> values);

	public APersistentCollection<T> deleteAll(final IPersistentCollection<T> values);

	// query methods
	
	@Override
	public boolean contains(Object o);

	@Override
	public boolean containsAll(Collection<?> c);
	
	public boolean containsAny(Collection<?> c);
	
	@Override
	public boolean isEmpty();
	
	// testing methods
	
	public void validate();
	
	@Override
	public ISeq<T> seq();
	
	/**
	 * Adds a value to this collection.
	 * Behaviour depends on the specific collection semantics
	 */
	public APersistentCollection<T> conj(T value);
	
}
