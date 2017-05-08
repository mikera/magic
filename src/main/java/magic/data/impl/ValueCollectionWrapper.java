package magic.data.impl;

import java.util.Iterator;
import java.util.Map;

import magic.data.APersistentCollection;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.ISeq;
import magic.data.Vectors;

/**
 * Wrapper for the value list of a map
 * @author Mike
 *
 * @param <K>
 * @param <V>
 */
public final class ValueCollectionWrapper<K,V> extends APersistentCollection<V> {
	private static final long serialVersionUID = 5958713253782492446L;

	
	APersistentSet<Map.Entry<K,V>> source;

	
	public ValueCollectionWrapper(APersistentSet<Map.Entry<K, V>> base) {
		source=base;
	}

	@Override
	public int size() {
		return source.size();
	}

	@Override
	public Iterator<V> iterator() {
		return new ValueCollectionIterator<K, V>(source);
	}
	
	public static class ValueCollectionIterator<K,V> implements Iterator<V> {
		private Iterator<Map.Entry<K,V>> source;
		
		public ValueCollectionIterator(APersistentSet<Map.Entry<K,V>> base) {
			source=base.iterator();
		}

		@Override
		public boolean hasNext() {
			return source.hasNext();
		}

		@Override
		public V next() {
			Map.Entry<K,V> next=source.next();
			return next.getValue();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public APersistentVector<V> include(V value) {
		return Vectors.createFromCollection(this).include(value);
	}

	@Override
	public ISeq<V> seq() {
		return Vectors.createFromCollection(this).seq();
	}

	@Override
	public APersistentVector<V> empty() {
		return Vectors.emptyVector();
	}

	@Override
	public Object valAt(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object valAt(Object key, Object notFound) {
		throw new UnsupportedOperationException();
	}

	@Override
	public APersistentCollection<V> assoc(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

}
