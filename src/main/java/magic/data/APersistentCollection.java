package magic.data;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import magic.RT;
import magic.data.impl.FilteredIterator;

/**
 * Abstract base class for all Magic collection types
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class APersistentCollection<T> extends APersistentObject implements IPersistentCollection<T>, IAssociative<Object,Object> {
	private static final long serialVersionUID = -962303316004942025L;

	@Override
	public abstract int size();
	
	@Override
	public APersistentCollection<T> clone() {
		return (APersistentCollection<T>)super.clone();
	}
	
	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Unsupported on immutable collection");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Unsupported on immutable collection");
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use delete(...) instead");
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use include(...) instead");
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use includeAll(...) instead");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use an empty instance instead");
	}
	
	@Override
	public boolean contains(Object o) {
		for (T it: this) {
			if (RT.equals(it, o)) return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object it: c) {
			if (!contains(it)) return false;
		}
		return true;
	}
	
	@Override
	public boolean containsAny(Collection<?> c) {
		for (Object it: c) {
			if (contains(it)) return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof APersistentCollection<?>) {
			return equals((APersistentCollection<T>)o);
		}
		return false;
	}
	
	public boolean equals(APersistentCollection<? extends T> pm) {
		return this.containsAll(pm)&&pm.containsAll(this);
	}
	
	@Override
	public int hashCode() {
		return RT.iteratorHashCode(this.iterator());
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}
	
	@Override
	public Object[] toArray() {
		Object[] os=new Object[size()];
		int i=0;
		for (T it: this) {
			os[i++]=it;
		}
		return os;
	}

	@Override
	public <V> V[] toArray(V[] a) {
		return toArray(a,0);
	}
	
	public <V> V[] toArray(V[] a, int offset) {
		int size=size();
		if (a.length<(size+offset)) {
			a=(V[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		int i=0;
		for (T it: this) {
			a[offset+(i++)]=(V)it;
		}
		return a;
	}

	@Override
	public APersistentCollection<T> exclude(final T value) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			@Override
			public boolean filter(Object testvalue) {
				return (!RT.equals(value, testvalue));
			}		
		};
		return empty().includeAll(Vectors.createFromIterator(it));
	}
	
	@Override
	public APersistentCollection<T> excludeAll(final IPersistentCollection<? extends T> values) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			@Override
			public boolean filter(Object value) {
				return (!values.contains(value));
			}		
		};
		return empty().includeAll(Vectors.createFromIterator(it));
	}

	@Override
	public APersistentCollection<T> excludeAll(final Collection<? extends T> values) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			APersistentCollection<T> col=Vectors.createFromCollection(values);
			
			@Override
			public boolean filter(Object value) {
				return (!col.contains(value));
			}		
		};
		return empty().includeAll(Vectors.createFromIterator(it));
	}
	
	@Override
	public Object assocIn(List<Object> keys, Object value) {
		int n=keys.size();
		if (n==0) return value;
		Object key=keys.get(0);
		if (n==1) return assoc(key,value);
		IAssociative<?,?> sub=((IAssociative<?,?>)valAt(0));
		if (sub==null) sub=(IAssociative<?,?>)Maps.EMPTY;
		return sub.assocIn(keys.subList(1, n),value);
	}
	
	@Override
	public abstract APersistentCollection<T> include(final T value);
	
	@Override
	public APersistentCollection<T> includeAll(final Collection<? extends T> values) {
		APersistentCollection<T> ps=this;
		for (T t: values) {
			ps=ps.include(t);
		}
		return ps;
	}
	
	@Override
	public APersistentCollection<T> includeAll(final IPersistentCollection<? extends T> values) {
		return includeAll((Collection<T>)values);
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("!{");
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
	public void validate() {
		// TODO: validation
	}
}
