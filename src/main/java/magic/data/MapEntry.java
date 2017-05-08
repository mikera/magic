package magic.data;

import java.util.Map;

import magic.Errors;
import magic.RT;

/**
 * Efficient MapEntry class, implemented as a 2 element vector
 * 
 * @author Mike
 *
 * @param <K>
 * @param <V>
 */
public final class MapEntry<K,V> extends APersistentVector<Object> implements Map.Entry<K,V> {
	private static final long serialVersionUID = -5217511002970709825L;

	private final K key;
	private final V value;
	private final int keyHash;
	
	public MapEntry(K key, V value) {
		this.key=key;
		this.value=value;
		keyHash=RT.hashCode(key);
	}
	
	public static <K,V> MapEntry<K,V> create(K key, V value) {
		return new MapEntry<K, V>(key,value);
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V arg0) {
		throw new UnsupportedOperationException(Errors.immutable(this));
	}

	@Override
	public int lastIndexOf(Object o) {
		if (RT.equals(value, o)) return 1;
		if (RT.equals(key, o)) return 0;
		return -1;
	}

	@Override
	public Object get(int i) {
		switch (i) {
		case 0: return key;
		case 1: return value;
		default: throw new IndexOutOfBoundsException(Errors.indexOutOfBounds(i));
		}
	}
	
	@Override
	public int hashCode() {
		int result=keyHash();
		result=Integer.rotateRight(result, 1);
		result^=RT.hashCode(value);
		return Integer.rotateRight(result, 1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o==null) return false;
		if (o==this) return true;
		if (o instanceof MapEntry) return equals((MapEntry<K, V>)o);
		return super.equals(o);
	}
	
	public boolean equals(MapEntry<K,V> me) {
		if (me==this) return true;
		if (me.keyHash!=keyHash) return false;
		if (!RT.equals(key, me.key)) return false;
		if (!RT.equals(value, me.value)) return false;
		return true;
	}

	private int keyHash() {
		return keyHash;
	}

	@Override
	public APersistentVector<Object> include(Object o) {
		return Tuple.wrap(new Object[]{key,value,o});
	}

	@Override
	public int size() {
		return 2;
	}



}
