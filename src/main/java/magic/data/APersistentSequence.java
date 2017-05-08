package magic.data;


/**
 * Abstract base type for sequence objects indexed by integers
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class APersistentSequence<T> extends APersistentCollection<T> {

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
	
	public abstract T get(int i);
}
