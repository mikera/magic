package magic.data;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import magic.RT;
import magic.data.impl.FilteredIterator;

@SuppressWarnings("unchecked")
@Immutable
public abstract class PersistentCollection<T> extends PersistentObject implements IPersistentCollection<T> {
	private static final long serialVersionUID = -962303316004942025L;

	@Override
	public abstract int size();
	
	@Override
	public PersistentCollection<T> clone() {
		return (PersistentCollection<T>)super.clone();
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
		if (o instanceof PersistentCollection<?>) {
			return equals((PersistentCollection<T>)o);
		}
		return false;
	}
	
	public boolean equals(PersistentCollection<T> pm) {
		return this.containsAll(pm)&&pm.containsAll(this);
	}
	
	@Override
	public int hashCode() {
		return RT.hashCode(this.iterator());
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
	public PersistentCollection<T> delete(final T value) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			@Override
			public boolean filter(Object testvalue) {
				return (!RT.equals(value, testvalue));
			}		
		};
		return ListFactory.createFromIterator(it);
	}
	
	@Override
	public PersistentCollection<T> deleteAll(final IPersistentCollection<T> values) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			@Override
			public boolean filter(Object value) {
				return (!values.contains(value));
			}		
		};
		return ListFactory.createFromIterator(it);
	}

	@Override
	public PersistentCollection<T> deleteAll(final Collection<T> values) {
		Iterator<T> it=new FilteredIterator<T>(iterator()) {
			PersistentCollection<T> col=ListFactory.createFromCollection(values);
			
			@Override
			public boolean filter(Object value) {
				return (!col.contains(value));
			}		
		};
		return ListFactory.createFromIterator(it);
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append('{');
		boolean first=true;
		for (T t: this) {
			if (first) {
				first=false;
			} else {
				sb.append(", ");
			}
			sb.append(t.toString());
		}		
		sb.append('}');
		return sb.toString();
	}
	
	@Override
	public abstract PersistentCollection<T> conj(final T value);
	
	@Override
	public PersistentCollection<T> includeAll(final Collection<T> values) {
		PersistentCollection<T> ps=this;
		for (T t: values) {
			ps=ps.conj(t);
		}
		return ps;
	}
	
	@Override
	public PersistentCollection<T> includeAll(final IPersistentCollection<T> values) {
		return includeAll((Collection<T>)values);
	}
	
	@Override
	public void validate() {
		// TODO: validation
	}
}
