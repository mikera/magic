package magic.data;

import java.io.Serializable;
import java.util.Collection;

public interface IPersistentCollection<T> extends Collection<T>, Cloneable, Serializable {

	// include methods
	
	public PersistentCollection<T> includeAll(final Collection<T> values);

	public PersistentCollection<T> includeAll(final IPersistentCollection<T> values);

	// delete methods
	
	public PersistentCollection<T> delete(final T value);
	
	public PersistentCollection<T> deleteAll(final Collection<T> values);

	public PersistentCollection<T> deleteAll(final IPersistentCollection<T> values);

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
	
	/**
	 * Returns the values in this collection as a sequence
	 */
	public ISeq<T> seq();
	
	/**
	 * Adds a value to this collection.
	 * Behaviour depends on the specific collection semantics
	 */
	public PersistentCollection<T> conj(T value);
	
}
