package magic.data;

import java.util.Collection;
import java.util.Iterator;

import magic.RT;
import magic.data.impl.FilteredIterator;
import magic.fn.IFn1;

public abstract class APersistentSet<T> extends APersistentCollection<T> implements IPersistentSet<T> {
	private static final long serialVersionUID = -6984657587635163165L;

	@Override
	public abstract APersistentSet<T> include(final T value);
	
	@Override
	public final APersistentSet<T> includeAll(final Collection<? extends T> values) {
		if (values instanceof APersistentSet<?>) return includeAll(values);
		APersistentSet<T> ps=this;
		for (T t: values) {
			ps=ps.include(t);
		}
		return ps;
	}
	
	public APersistentSet<T> includeAll(final APersistentSet<? extends T> values) {
		// TODO: use merge
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
	
	@Override
	public final  boolean containsKey(Object key) {
		return contains(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final APersistentSet<T> assoc(Object key,Object value) {
		return include((T) key);		
	}
	
	/**
	 * Default implementation for include
	 * Note: should be overridden if faster implementation is possible
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public APersistentSet<T> includeAll(final IPersistentSet<? extends T> values) {
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

	@SuppressWarnings("unchecked")
	@Override
	public final APersistentSet<T> excludeAll(Collection<? extends T> values) {
		if (values instanceof APersistentSet<?>) return excludeAll((APersistentSet<T>)values);
		final APersistentSet<T> col=Sets.createFrom(values);
		Iterator<T> it=new FilteredIterator<T>(iterator()) {	
			@Override
			public boolean filter(Object value) {
				return (!col.contains(value));
			}		
		};
		return Sets.createFrom(it);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final APersistentSet<T> excludeAll(IPersistentCollection<? extends T> values) {
		if (values instanceof APersistentSet<?>) return excludeAll((APersistentSet<T>)values);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public final APersistentSet<T> excludeAll(final APersistentCollection<? extends T> values) {
		if (values instanceof APersistentSet<?>) return excludeAll((APersistentSet<T>)values);
		Iterator<T> it=new FilteredIterator<T>(iterator()) {

			@Override
			public boolean filter(Object value) {
				return (!values.contains(value));
			}		
		};
		return Sets.createFrom(it);
	}
	
	public APersistentSet<T> excludeAll(final APersistentSet<T> values) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {

			@Override
			public boolean filter(Object value) {
				return (!values.contains(value));
			}		
		};
		return Sets.createFrom(it);
	}
	
	@Override
	public <R> APersistentSet<R> map(IFn1<? super T, ? extends R> f) {
		APersistentSet<R> r=Sets.create();
		for (T t: this) {
			r=r.include(f.apply(t));
		}
		return r;
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
