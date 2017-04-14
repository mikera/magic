package magic.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import magic.data.impl.BlockList;
import magic.data.impl.NullList;
import magic.data.impl.SingletonList;

/**
 * Factory class for persistent list types
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public class ListFactory<T> {
	public static final int TUPLE_BUILD_BITS=5;
	public static final int MAX_TUPLE_BUILD_SIZE=1<<TUPLE_BUILD_BITS;
	
	public static PersistentList<?>[] NULL_PERSISTENT_LIST_ARRAY=new PersistentList[0];
	
	public static <T> IPersistentList<T> create() {
		return emptyList();
	}	
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentList<T> emptyList() {
		return (PersistentList<T>) NullList.INSTANCE;
	}	
	
	public static <T> PersistentList<T> of(T value) {
		return SingletonList.of(value);
	}
	
	public static <T> PersistentList<T> of(T a, T b) {
		return Tuple.of(a,b);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentList<T> of(T... values) {
		return createFromArray(values,0,values.length);
	}
	
	public static <T> PersistentList<T> createFromArray(T[] data) {
		return createFromArray(data,0,data.length);
	}
	
	public static <T> PersistentList<T> createFromArray(T[] data,  int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<=MAX_TUPLE_BUILD_SIZE) {
			// very small cases
			if (n<2) {
				if (n<0) throw new IllegalArgumentException(); 
				if (n==0) return emptyList();
				return SingletonList.of(data[fromIndex]);
			}	
			
			// note this covers negative length case
			return Tuple.create(data,fromIndex,toIndex);
		}	
		
		// otherwise create a block list
		return BlockList.create(data,fromIndex,toIndex);
	}

	@SuppressWarnings("unchecked")
	public static <T> PersistentList<T> createFromCollection(Collection<T> source) {
		if (source instanceof PersistentList<?>) {
			return (PersistentList<T>)source;
		} else if (source instanceof List<?>) {
			return createFromList((List<T>)source,0,source.size());
		} 
		
		Object[] data=source.toArray();
		return createFromArray((T[])data);
	}
	
	public static<T> PersistentList<T> createFromIterator(Iterator<T> source) {
		ArrayList<T> al=new ArrayList<T>();
		while(source.hasNext()) {
			al.add(source.next());
		}
		return createFromCollection(al);
	}
	
	public static<T> PersistentList<T> subList(List<T> list, int fromIndex, int toIndex) {
		return createFromList(list,fromIndex,toIndex);
	}

	public static <T> PersistentList<T> createFromList(List<T> source) {
		return createFromList(source,0,source.size());
	}
	
	public static <T> PersistentList<T> createFromList(List<T> source, int fromIndex, int toIndex) {
		int maxSize=source.size();
		if ((fromIndex<0)||(toIndex>maxSize)) throw new IndexOutOfBoundsException();
		int newSize=toIndex-fromIndex;
		if (newSize<=0) {
			if (newSize==0) return emptyList();
			throw new IllegalArgumentException();
		}
			
		// use sublist if possible
		if (source instanceof PersistentList) {
			if (newSize==maxSize) return (PersistentList<T>)source;
			return createFromList((IPersistentList<T>)source,fromIndex, toIndex);
		}
		
		if (newSize==1) return SingletonList.of(source.get(fromIndex));
		if (newSize<=MAX_TUPLE_BUILD_SIZE) {
			// note this covers negative length case
			return Tuple.createFrom(source,fromIndex,toIndex);
		}
		
		// create blocklist for larger lists
		return BlockList.create(source, fromIndex, toIndex);
	}
	
	public static <T> PersistentList<T> createFromList(IPersistentList<T> source, int fromIndex, int toIndex) {
		return createFromList(source).subList(fromIndex, toIndex);
	}
	
	public static <T> PersistentList<T> concat(List<T> a, List<T> b) {
		return concat(createFromList(a), createFromList(b));
	}
	
	public static <T> PersistentList<T> concat(PersistentList<T> a, PersistentList<T> b) {
		return a.concat(b);
	}
}
