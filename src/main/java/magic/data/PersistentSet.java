package magic.data;

import java.util.Collection;
import java.util.Iterator;

import magic.RT;
import magic.data.impl.FilteredIterator;

public abstract class PersistentSet<T> extends PersistentCollection<T> implements IPersistentSet<T> {
	private static final long serialVersionUID = -6984657587635163165L;

	@Override
	public abstract PersistentSet<T> conj(final T value);
	
	@Override
	public PersistentSet<T> includeAll(final Collection<T> values) {
		PersistentSet<T> ps=this;
		for (T t: values) {
			ps=ps.conj(t);
		}
		return ps;
	}
	
	/**
	 * Default implementation for include
	 * Note: should be overridden if faster implementation is possible
	 * @param values
	 * @return
	 */
	@Override
	public PersistentSet<T> includeAll(final IPersistentSet<T> values) {
		return includeAll((Collection<T>)values);
	}

	@Override
	public PersistentSet<T> clone() {
		return (PersistentSet<T>)super.clone();
	}
	
	@Override
	public PersistentSet<T> delete(final T value) {
		if (!contains(value)) return this;
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			@Override
			public boolean filter(Object testvalue) {
				return (!RT.equals(value, testvalue));
			}		
		};
		return SetFactory.createFrom(it);
	}

	@Override
	public PersistentSet<T> deleteAll(final Collection<T> values) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			PersistentCollection<T> col=ListFactory.createFromCollection(values);
			
			@Override
			public boolean filter(Object value) {
				return (!col.contains(value));
			}		
		};
		return SetFactory.createFrom(it);
	}
	
	@Override
	public PersistentSet<T> deleteAll(final PersistentCollection<T> values) {
		if ( values==null) throw new Error();
		Iterator<T> it=new FilteredIterator<T>(iterator()) {

			@Override
			public boolean filter(Object value) {
				return (!values.contains(value));
			}		
		};
		return SetFactory.createFrom(it);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if ((o instanceof PersistentSet<?>)) return equals((PersistentSet<T>)o);
		return false;
	}
	
	public boolean equals(PersistentSet<T> s) {
		if (size()!=s.size()) return false;
		return s.containsAll(this)&&(this.containsAll(s));
	}
	
	@Override
	public boolean allowsNulls() {
		return true;
	}
	
	@Override
	public int hashCode() {
		return RT.iteratorHashCode(iterator());
	}
}
