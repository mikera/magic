package magic.data;

import java.util.Collection;
import java.util.Map;

public interface IPersistentMap<K,V> extends Map<K,V>, IPersistentObject, ISeqable<Map.Entry<K,V>>, IAssociative<K,V> {
	// include methods
	
	public IPersistentMap<K,V> assoc(K key, V value);
	
	public IPersistentMap<K,V> include(Map<K,V> values);

	public IPersistentMap<K,V> include(IPersistentMap<K,V> values);

	// delete methods

	public IPersistentMap<K,V> dissoc(K key);
	
	/**
	 * Deletes the specified keys from the map
	 * @param key
	 * @return
	 */
	public IPersistentMap<K,V> delete(Collection<K> keys);

	/**
	 * Deletes the specified keys from the map
	 * @param key
	 * @return
	 */
	public IPersistentMap<K,V> delete(IPersistentSet<K> keys);
	
	// query methods
	
	public boolean allowsNullKey();
}
