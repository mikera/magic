package magic.data;

import java.util.List;
import java.util.Map;

import magic.data.impl.NullMap;

/**
 * Factory class for persistent map types
 * 
 * @author Mike Anderson
 *
 */
public class Maps {
	public static final APersistentMap<?, ?> EMPTY = NullMap.INSTANCE;

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
	
	@SuppressWarnings("unchecked")
	public static <K,V> APersistentMap<? extends K, ? extends V> createFromFlattenedPairs(List<?> values) {
		int n=values.size();
		if ((n&1)!=0) throw new Error("Map construction requires an even number of terms");
		APersistentMap<K,V> m=(APersistentMap<K, V>) PersistentHashMap.EMPTY;
		for (int i=0; i<n; i+=2) {
			m=m.assoc((K)values.get(i), (V)values.get(i+1));
		}
		return m;
	}

	@SuppressWarnings("unchecked")
	public static <K,V> APersistentMap<? extends K, ? extends V> createFromFlattenedArray(Object[] values) {
		int n=values.length;
		if ((n&1)!=0) throw new Error("Map construction requires an even number of terms");
		APersistentMap<K,V> m=(APersistentMap<K, V>) PersistentHashMap.EMPTY;
		for (int i=0; i<n; i+=2) {
			m=m.assoc((K)values[i], (V)values[i+1]);
		}
		return m;
	}

	@SuppressWarnings("unchecked")
	public static <K,V> APersistentMap<K, V> empty() {
		return (APersistentMap<K, V>) EMPTY;
	}
}
