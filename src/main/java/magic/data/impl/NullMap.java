package magic.data.impl;

import java.io.ObjectStreamException;
import java.util.List;
import java.util.Map;

import magic.data.APersistentCollection;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.PersistentHashMap;

public final class NullMap<K,V> extends APersistentMap<K, V> {
	private static final long serialVersionUID = 1717634837542733926L;

	
	@SuppressWarnings({ "rawtypes" })
	public static final NullMap<?,?> INSTANCE=new NullMap();
	
	private NullMap() {
		
	}

	@Override
	public V valAt(K key) {
		return null;
	}

	@Override
	public V valAt(K key, V notFound) {
		return notFound;
	}
	
	@Override
	public void clear() {
		// We are already empty, so nothing to do
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public APersistentSet<java.util.Map.Entry<K, V>> entrySet() {
		return (APersistentSet<java.util.Map.Entry<K, V>>) NullSet.INSTANCE;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public APersistentSet<K> keySet() {
		return (APersistentSet<K>) NullSet.INSTANCE;
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}



	@Override
	public int size() {
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public APersistentCollection<V> values() {
		return (APersistentCollection<V>) NullSet.INSTANCE;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object key) {
		return null;
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public NullMap<K,V> clone() {
		return this;
	}

	@Override
	public APersistentMap<K, V> assoc(K key, V value) {
		return PersistentHashMap.create(key,value);
	}

	@Override
	public APersistentMap<K, V> dissoc(K key) {
		return this;
	}

	@Override
	public java.util.Map.Entry<K, V> getMapEntry(Object key) {
		return null;
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}
	
	@Override
	public boolean allowsNullKey() {
		return false;
	}

	@Override
	public Object assocIn(List<Object> keys, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
}
