package magic.data;

import java.util.Collection;
import java.util.Map;

public interface IPersistentMap<K,V> extends Map<K,V>, IPersistentObject, Sequable<Map.Entry<K,V>> {
	// include methods
	
	public IPersistentMap<K,V> include(K key, V value);
	
	public IPersistentMap<K,V> include(Map<K,V> values);

	public IPersistentMap<K,V> include(IPersistentMap<K,V> values);

	// delete methods

	public IPersistentMap<K,V> delete(K key);
	
	public IPersistentMap<K,V> delete(Collection<K> key);

	public IPersistentMap<K,V> delete(IPersistentSet<K> key);
	
	// query methods
	
	public boolean allowsNullKey();
}
