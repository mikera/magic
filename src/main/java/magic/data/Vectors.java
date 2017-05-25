package magic.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import magic.data.impl.EmptyVector;
import magic.data.impl.RepeatVector;

/**
 * Static function class for persistent list types
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public class Vectors<T> {
	public static final int TUPLE_BUILD_BITS=5;
	public static final int MAX_TUPLE_BUILD_SIZE=1<<TUPLE_BUILD_BITS;
	
	public static APersistentVector<?>[] NULL_PERSISTENT_LIST_ARRAY=new APersistentVector[0];
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentVector<T> create() {
		return (APersistentVector<T>) emptyVector();
	}	
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentVector<T> emptyVector() {
		return (APersistentVector<T>) EmptyVector.INSTANCE;
	}	
	
	public static <T> APersistentVector<T> of(T value) {
		return RepeatVector.create(value,1);
	}
	
	public static <T> APersistentVector<T> of(T a, T b) {
		return Tuple.of(a,b);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentVector<T> of(T... values) {
		return createFromArray(values,0,values.length);
	}
	
	public static <T> APersistentVector<T> createFromArray(T[] data) {
		return createFromArray(data,0,data.length);
	}
	
	public static <T> Tuple<T> wrap(T[] data) {
		return Tuple.wrap(data,0,data.length);
	}
	
	/**
	 * Creates a persistent list from the specified values
	 * @param data
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public static <T> APersistentVector<T> createFromArray(T[] data,  int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<=MAX_TUPLE_BUILD_SIZE) {
			// very small cases
			if (n<2) {
				if (n<0) throw new IllegalArgumentException(); 
				if (n==0) return emptyVector();
				return RepeatVector.create(data[fromIndex],1);
			}	
			
			// note this covers negative length case
			return Tuple.create(data,fromIndex,toIndex);
		}	
		
		// otherwise create a block list
		return PersistentVector.create(data,fromIndex,toIndex);
	}

	@SuppressWarnings("unchecked")
	public static <T> APersistentVector<T> createFromCollection(Collection<? extends T> source) {
		if (source instanceof APersistentVector<?>) {
			return (APersistentVector<T>)source;
		} else if (source instanceof List<?>) {
			return createFromList((List<T>)source,0,source.size());
		} 
		
		Object[] data=source.toArray();
		return createFromArray((T[])data);
	}
	
	public static<T> APersistentVector<T> createFromIterator(Iterator<T> source) {
		ArrayList<T> al=new ArrayList<T>();
		while(source.hasNext()) {
			al.add(source.next());
		}
		return createFromCollection(al);
	}
	
	public static<T> APersistentVector<T> subList(List<T> list, int fromIndex, int toIndex) {
		return createFromList(list,fromIndex,toIndex);
	}

	public static <T> APersistentVector<T> createFromList(List<? extends T> source) {
		return createFromList(source,0,source.size());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentVector<T> createFromList(List<? extends T> source, int fromIndex, int toIndex) {
		int maxSize=source.size();
		if ((fromIndex<0)||(toIndex>maxSize)) throw new IndexOutOfBoundsException();
		int newSize=toIndex-fromIndex;
		if (newSize<=0) {
			if (newSize==0) return emptyVector();
			throw new IllegalArgumentException();
		}
			
		// use sublist if possible
		if (source instanceof APersistentVector) {
			if (newSize==maxSize) return (APersistentVector<T>)source;
			return createFromList((APersistentVector<T>)source,fromIndex, toIndex);
		}
		
		if (newSize==1) return RepeatVector.create(source.get(fromIndex),1);
		if (newSize<=MAX_TUPLE_BUILD_SIZE) {
			// note this covers negative length case
			return Tuple.createFrom(source,fromIndex,toIndex);
		}
		
		// create blocklist for larger lists
		return PersistentVector.create((List<T>)source, fromIndex, toIndex);
	}
	
	public static <T> APersistentVector<T> createFromList(APersistentVector<T> source, int fromIndex, int toIndex) {
		return source.subList(fromIndex, toIndex);
	}
	
	public static <T> APersistentVector<T> concat(List<T> a, List<T> b) {
		return concat(createFromList(a), createFromList(b));
	}
	
	public static <T> APersistentVector<T> concat(APersistentVector<T> a, APersistentVector<T> b) {
		return a.concat(b);
	}

	/**
	 * Coerces any collection to a persistent vector
	 * @param a
	 * @return
	 */
	public static <T> APersistentVector<T> coerce(IPersistentCollection<T> a) {
		if (a instanceof APersistentVector<?>) return (APersistentVector<T>)a;
		return Vectors.createFromCollection(a);
	}
	
	/**
	 * Coerces any collection to a persistent vector
	 * @param a
	 * @return
	 */
	public static <T> APersistentVector<T> coerce(APersistentCollection<T> a) {
		if (a instanceof APersistentVector<?>) return (APersistentVector<T>)a;
		return Vectors.createFromCollection(a);
	}

	public static <T> APersistentVector<T> coerce(Collection<T> a) {
		if (a instanceof APersistentVector<?>) return (APersistentVector<T>)a;
		return Vectors.createFromCollection(a);
	}
}
