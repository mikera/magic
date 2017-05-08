package magic.data;

import java.util.Collection;
import java.util.List;

public interface IPersistentCollection<T> extends Collection<T>, ISeqable<T> {

	// include methods
	
	/**
	 * Adds a value to this collection.
	 * Behaviour depends on the specific collection semantics, equivalent to Clojure conj
	 */
	public APersistentCollection<T> include(T value);
	
	/**
	 * Adds all values from another collection into this collection.
	 * Behaviour depends on the specific collection semantics, equivalent to Clojure into
	 */
	public APersistentCollection<T> includeAll(final Collection<T> values);

	/**
	 * Adds all values from another collection into this collection.
	 * Behaviour depends on the specific collection semantics, equivalent to Clojure into
	 */
	public APersistentCollection<T> includeAll(final IPersistentCollection<T> values);

	// delete methods
	
	/**
	 * Removes all instances of a value from this collection.
	 * Behaviour depends on the specific collection semantics.
	 */
	public APersistentCollection<T> exclude(final T value);
	
	/**
	 * Removes all instances of a collection of values from this collection.
	 * Behaviour depends on the specific collection semantics.
	 */
	public APersistentCollection<T> excludeAll(final Collection<T> values);

	/**
	 * Removes all instances of a collection of values from this collection.
	 * Behaviour depends on the specific collection semantics.
	 */
	public APersistentCollection<T> excludeAll(final IPersistentCollection<T> values);

	// query methods
	
	@Override
	public boolean contains(Object o);
	
	/**
	 * Returns true if the collection contains the specified key.
	 */
	public boolean containsKey(Object o);
	
	/**
	 * Gets the value at a specific key position
	 * Returns null if not found
	 */
	public Object valAt(Object key);
	
	/**
	 * Accocs into a nested structure
	 * Returns null if not found
	 */
	public Object assocIn(List<Object> keys, Object value);
	
	/**
	 * Gets the value at a specific key position
	 * Returns notFound if not found
	 */
	public Object valAt(Object key,Object notFound);
	
	/**
	 * Updates this collection by including the specified value at the given key position.
	 * The same value can subsequently be retrieved with valAt using the same key.
	 * @throws IllegalArgumentException if the key or value is not supported by the collection.
	 */
	public APersistentCollection<T> assoc(Object key,Object value);
	
	
	@Override
	public boolean containsAll(Collection<?> c);
	
	/**
	 * Returns true if this collection contains any of the elements in the specified collection.
	 * @param c Another collection of elements to test
	 * @return
	 */
	public boolean containsAny(Collection<?> c);
	
	@Override
	public boolean isEmpty();
	
	// testing methods
	
	public void validate();
	
	@Override
	public ISeq<T> seq();
	
	/**
	 * Returns an empty collection of the same collection type as this collection.
	 * @return
	 */
	public APersistentCollection<T>  empty();
	
}
