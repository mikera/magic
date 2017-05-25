package magic.data;

import java.util.Collection;
import java.util.List;

import magic.fn.AFn1;
import magic.fn.IFn1;

/**
 * Abstract base type for sequence objects indexed by integers
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class APersistentSequence<T> extends APersistentCollection<T> implements List<T> {

	@Override
	public final Object valAt(Object key) {
		if (key instanceof Number) {
			return valAt((Number)key,null);
		}
		return null;
	}

	@Override
	public final Object valAt(Object key,Object notFound) {
		if (key instanceof Number) {
			return valAt((Number)key,null);
		}
		return null;		
	}
	
	public Object valAt(Number key,Object notFound) {
		int k=key.intValue();
		if ((key.doubleValue()!=k)||(k<0)||(k>=size())) return notFound;
		return get(k);		
	}
	
	@Override
	public final  boolean containsKey(Object key) {
		if (key instanceof Number) {
			Number num=(Number) key;
			int k=num.intValue();
			if ((num.doubleValue()!=k)||(k<0)||(k>=size())) return false;
			return true;
		}
		return false;
	}
	
	public abstract <R> APersistentSequence<R> map(IFn1<? super T, ? extends R> f);
	
	@Override
	public abstract T get(int i);
	
	/**
	 * Returns a subset of the given list
	 * Can be the whole list, or an empty list
	 */
	@Override
	public abstract APersistentSequence<T> subList(int fromIndex, int toIndex);

	public abstract APersistentSequence<T> insertAll(int index, Collection<? extends T> values);
}
