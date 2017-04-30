package magic.data.impl;

import java.util.Iterator;
import java.util.Map;

import magic.data.ISeq;
import magic.data.Lists;
import magic.data.APersistentCollection;
import magic.data.APersistentList;
import magic.data.PersistentSet;

public final class ValueCollectionWrapper<K,V> extends APersistentCollection<V> {
	private static final long serialVersionUID = 5958713253782492446L;

	
	PersistentSet<Map.Entry<K,V>> source;

	
	public ValueCollectionWrapper(PersistentSet<Map.Entry<K, V>> base) {
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
		
		public ValueCollectionIterator(PersistentSet<Map.Entry<K,V>> base) {
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
	public APersistentList<V> conj(V value) {
		return Lists.createFromCollection(this).conj(value);
	}

	@Override
	public ISeq<V> seq() {
		return Lists.createFromCollection(this).seq();
	}
}
