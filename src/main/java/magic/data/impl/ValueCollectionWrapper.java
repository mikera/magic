package magic.data.impl;

import java.util.Iterator;
import java.util.Map;

import magic.data.ISeq;
import magic.data.ListFactory;
import magic.data.PersistentCollection;
import magic.data.PersistentList;
import magic.data.PersistentSet;

public final class ValueCollectionWrapper<K,V> extends PersistentCollection<V> {
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
	public PersistentList<V> conj(V value) {
		return ListFactory.createFromCollection(this).conj(value);
	}

	@Override
	public ISeq<V> seq() {
		return ListFactory.createFromCollection(this).seq();
	}
}
