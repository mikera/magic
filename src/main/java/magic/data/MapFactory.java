package magic.data;

import java.util.Map;

import magic.data.impl.NullMap;
import magic.data.impl.PersistentHashMap;

/**
 * Factory class for persistent map types
 * 
 * @author Mike Anderson
 *
 */
public class MapFactory {
	@SuppressWarnings("unchecked")
	public static <K,V> PersistentMap<K,V> create() {
		return (PersistentMap<K,V>)NullMap.INSTANCE;
	}
	
	public static <K,V> PersistentMap<K,V> create(K key, V value) {
		return PersistentHashMap.create(key, value);
	}
	
	public static <K,V> PersistentMap<K,V> create(Map<K,V> values) {
		return PersistentHashMap.create(values);
	}
}
