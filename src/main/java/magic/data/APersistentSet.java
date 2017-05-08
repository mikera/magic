package magic.data;

import java.util.Collection;
import java.util.Iterator;

import magic.RT;
import magic.data.impl.FilteredIterator;

public abstract class APersistentSet<T> extends APersistentCollection<T> implements IPersistentSet<T> {
	private static final long serialVersionUID = -6984657587635163165L;

	@Override
	public abstract APersistentSet<T> include(final T value);
	
	@Override
	public APersistentSet<T> includeAll(final Collection<T> values) {
		APersistentSet<T> ps=this;
		for (T t: values) {
			ps=ps.include(t);
		}
		return ps;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final T valAt(Object key) {
		if (contains(key)) return (T) key;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final T valAt(Object key,Object notFound) {
		if (contains(key)) return (T) key;
		return (T) notFound;		
	}
	
	/**
	 * Default implementation for include
	 * Note: should be overridden if faster implementation is possible
	 * @param values
	 * @return
	 */
	@Override
	public APersistentSet<T> includeAll(final IPersistentSet<T> values) {
		return includeAll((Collection<T>)values);
	}

	@Override
	public APersistentSet<T> clone() {
		return (APersistentSet<T>)super.clone();
	}
	
	@Override
	public APersistentSet<T> exclude(final T value) {
		if (!contains(value)) return this;
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			@Override
			public boolean filter(Object testvalue) {
				return (!RT.equals(value, testvalue));
			}		
		};
		return Sets.createFrom(it);
	}

	@Override
	public APersistentSet<T> excludeAll(Collection<T> values) {
		final APersistentSet<T> col=Sets.createFrom(values);
		Iterator<T> it=new FilteredIterator<T>(iterator()) {	
			@Override
			public boolean filter(Object value) {
				return (!col.contains(value));
			}		
		};
		return Sets.createFrom(it);
	}
	
	@Override
	public APersistentSet<T> excludeAll(T[] values) {
		final APersistentSet<T> col=Sets.createFrom(values);
		Iterator<T> it=new FilteredIterator<T>(iterator()) {	
			@Override
			public boolean filter(Object value) {
				return (!col.contains(value));
			}		
		};
		return Sets.createFrom(it);
	}
	
	@Override
	public APersistentSet<T> excludeAll(final APersistentCollection<T> values) {
		if ( values==null) throw new Error();
		Iterator<T> it=new FilteredIterator<T>(iterator()) {

			@Override
			public boolean filter(Object value) {
				return (!values.contains(value));
			}		
		};
		return Sets.createFrom(it);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if ((o instanceof APersistentSet<?>)) return equals((APersistentSet<T>)o);
		return false;
	}
	
	public boolean equals(APersistentSet<T> s) {
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
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("#{");
		boolean first=true;
		for (T t: this) {
			if (first) {
				first=false;
			} else {
				sb.append(' ');
			}
			sb.append(RT.toString(t));
		}		
		sb.append('}');
		return sb.toString();
	}
	
	@Override
	public APersistentSet<T> empty() {
		return Sets.emptySet();
	}
}
