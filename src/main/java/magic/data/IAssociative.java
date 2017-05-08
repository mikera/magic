package magic.data;

import java.util.List;

public interface IAssociative<K,V> {

	/**
	 * Gets the value at a specific key position
	 * Returns null if not found
	 */
	public V valAt(K key);
	
	/**
	 * Gets the value at a specific key position
	 * Returns notFound if not found
	 */
	public V valAt(K key,V notFound);
	
	/**
	 * Assocs into a nested structure
	 * Returns null if not found
	 */
	public Object assocIn(List<Object> keys, Object value);
}
