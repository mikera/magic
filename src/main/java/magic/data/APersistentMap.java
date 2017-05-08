package magic.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import magic.RT;

/**
 * Abstract base class for persistent maps.
 * 
 * @author Mike
 *
 * @param <K>
 * @param <V>
 */
public abstract class APersistentMap<K,V> extends APersistentObject implements IPersistentMap<K,V> {
	private static final long serialVersionUID = 2304218229796144868L;

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use an empty instance");
	}

	@Override
	public abstract boolean containsKey(Object arg0);

	@Override
	public boolean containsValue(Object value) {
		for (Map.Entry<K,V> ent: entrySet()) {
			if (RT.equals(ent.getValue(),value)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsEntry(Map.Entry<K,V> entry) {
		Map.Entry<K,V> e=getMapEntry(entry.getKey());
		if (e==null) return false;
		return RT.equals(e.getValue(), entry.getValue());
	}
	
	public abstract java.util.Map.Entry<K, V> getMapEntry(Object key);

	@Override
	public abstract APersistentSet<java.util.Map.Entry<K, V>> entrySet();

	@Override
	public abstract V get(Object key);

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public abstract APersistentSet<K> keySet();

	@Override
	public V put(K arg0, V arg1) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use include(...) instead");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object arg0) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use delete(...) instead");
	}

	@Override
	public abstract int size();

	@Override
	public abstract APersistentCollection<V> values();

	@Override
	@SuppressWarnings("unchecked")
	public APersistentMap<K,V> clone() {
		return (APersistentMap<K,V>)super.clone();
	}
	
	@Override
	public abstract APersistentMap<K, V> dissoc(K key);

	@Override
	public APersistentMap<K, V> delete(Collection<K> keys) {
		APersistentMap<K, V> pm=this;
		for (K k: keys) {
			pm=pm.dissoc(k);
		}
		return pm;
	}

	@Override
	public APersistentMap<K, V> delete(IPersistentSet<K> keys) {
		return delete((Collection<K>) keys);
	}

	@Override
	public abstract APersistentMap<K, V> assoc(K key, V value);

	@Override
	public APersistentMap<K, V> include(Map<K, V> values) {
		APersistentMap<K, V> pm=this;
		for (Map.Entry<K, V> entry:values.entrySet()) {
			pm=pm.assoc(entry.getKey(),entry.getValue());
		}
		return pm;
	}

	@Override
	public APersistentMap<K, V> include(IPersistentMap<K, V> values) {
		return include((Map<K,V>) values);
	}
	
	public HashMap<K,V> toHashMap() {
		HashMap<K,V> hm=new HashMap<K, V>();
		for (Map.Entry<K,V> ent: entrySet()) {
			hm.put(ent.getKey(), ent.getValue());
		}
		return hm;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof APersistentMap<?,?>) {
			return equals((APersistentMap<K,V>)o);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return RT.hashCode(entrySet().iterator());
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}
	
	public boolean equals(APersistentMap<K,V> pm) {
		if (this==pm) return true;
		if (this.size()!=pm.size()) return false;
		return this.containsAll(pm)&&pm.containsAll(this);
	}
	
	public boolean containsAll(APersistentMap<K,V> pm) {
		for (Map.Entry<K, V> ent:pm.entrySet()) {
			if (!containsEntry(ent)) return false;
		}
		return true;
	}

	@Override
	public void validate() {
		// nothing to do
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append('{');
		boolean first=true;
		for (Map.Entry<K, V> ent: entrySet()) {
			if (first) {
				first=false;
			} else {
				sb.append(", ");
			}
			sb.append(ent.toString());
		}
		sb.append('}');
		return sb.toString();
	}
	
	@Override
	public ISeq<java.util.Map.Entry<K, V>> seq() {
		return Tools.seq(this.entrySet().iterator());
	}
}
