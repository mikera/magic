package magic.data.impl;

import java.util.Iterator;
import java.util.Map;

import magic.data.PersistentHashSet;
import magic.data.PersistentSet;

/**
 * Wrapper for the key set of a persistent map
 * @author Mike
 *
 * @param <K>
 * @param <V>
 */
public final class KeySetWrapper<K,V> extends BasePersistentSet<K> {
	private static final long serialVersionUID = -3297453356838115646L;

	
	PersistentSet<Map.Entry<K,V>> source;
	
	
	public KeySetWrapper(PersistentSet<Map.Entry<K, V>> base) {
		source=base;
	}
	
	@Override
	public PersistentSet<K> conj(K value) {
		return PersistentHashSet.coerce(this).conj(value);
	}

	@Override
	public int size() {
		return source.size();
	}

	@Override
	public Iterator<K> iterator() {
		return new KeySetIterator<K, V>(source);
	}
	
	public static class KeySetIterator<K,V> implements Iterator<K> {
		private Iterator<Map.Entry<K,V>> source;
		
		public KeySetIterator(PersistentSet<Map.Entry<K,V>> base) {
			source=base.iterator();
		}

		@Override
		public boolean hasNext() {
			return source.hasNext();
		}

		@Override
		public K next() {
			Map.Entry<K,V> next=source.next();
			return next.getKey();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
