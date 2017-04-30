package magic.data;

import java.util.Map;

import magic.data.impl.NullMap;

/**
 * Factory class for persistent map types
 * 
 * @author Mike Anderson
 *
 */
public class Maps {
	@SuppressWarnings("unchecked")
	public static <K,V> APersistentMap<K,V> create() {
		return (APersistentMap<K,V>)NullMap.INSTANCE;
	}
	
	public static <K,V> APersistentMap<K,V> create(K key, V value) {
		return PersistentHashMap.create(key, value);
	}
	
	public static <K,V> APersistentMap<K,V> create(Map<K,V> values) {
		return PersistentHashMap.create(values);
	}
}
