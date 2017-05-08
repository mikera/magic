package magic.data;

import java.io.Serializable;
import java.util.Collection;

public interface IPersistentCollection<T> extends Collection<T>, ISeqable<T>, Cloneable, Serializable {

	// include methods
	
	/**
	 * Adds a value to this collection.
	 * Behaviour depends on the specific collection semantics
	 */
	public APersistentCollection<T> include(T value);
	
	public APersistentCollection<T> includeAll(final Collection<T> values);

	public APersistentCollection<T> includeAll(final IPersistentCollection<T> values);

	// delete methods
	
	public APersistentCollection<T> exclude(final T value);
	
	public APersistentCollection<T> excludeAll(final Collection<T> values);

	public APersistentCollection<T> excludeAll(final IPersistentCollection<T> values);

	// query methods
	
	@Override
	public boolean contains(Object o);
	
	/**
	 * Gets the value at a specific key position
	 * Returns null if not found
	 */
	public Object valAt(Object key);
	
	/**
	 * Gets the value at a specific key position
	 * Returns notFound if not found
	 */
	public Object valAt(Object key,Object notFound);
	
	
	@Override
	public boolean containsAll(Collection<?> c);
	
	public boolean containsAny(Collection<?> c);
	
	@Override
	public boolean isEmpty();
	
	// testing methods
	
	public void validate();
	
	@Override
	public ISeq<T> seq();
	
	public APersistentCollection<T>  empty();
	
}
