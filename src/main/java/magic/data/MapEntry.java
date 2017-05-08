package magic.data;

import java.util.Map;

import magic.Errors;
import magic.RT;

public class MapEntry<K,V> extends APersistentVector<Object> implements Map.Entry<K,V> {
	private static final long serialVersionUID = -5217511002970709825L;

	private final K key;
	private final V value;
	
	public MapEntry(K key, V value) {
		this.key=key;
		this.value=value;
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
	public APersistentVector<Object> include(Object o) {
		return Tuple.of(key,value,o);
	}

	@Override
	public int size() {
		return 2;
	}

}
