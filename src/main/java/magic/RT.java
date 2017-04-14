package magic;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Static class to support the Magic runtime
 * 
 * @author Mike
 */
public class RT {

	/**
	 * Converts an object to a boolean primitive value, according to Clojure's truthiness rules
	 * i.e. nil or Boolean.FALSE is false, everything else is truthy
	 * @param o
	 * @return
	 */
	public static boolean bool(Object o) {
		if (o == Boolean.FALSE) return false;
		return (o!=null);
	}
	
	/**
	 * Returns true if and only if two Objects are equal.
	 * Handles nulls as distinct values that are equal to null only.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static final boolean equals(Object a, Object b) {
		if (a==b) return true;
		if ((a==null)||(b==null)) return false;
		return a.equals(b);
	}
	
	/**
	 * Compares two Comparable values, considering null as the lowest possible value 
	 * 
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T extends Comparable<? super T>> int compare(T a, T b) {
		if (a==b) return 0;
		if (a==null) return -1;
		if (b==null) return 1;
		return a.compareTo(b);
	}
	
	/**
	 * Creates a HashSet containing the value from the given iterator. Removes duplicates.
	 * @param iterator
	 * @return
	 */
	public static <T> HashSet<T> buildHashSet(Iterator<T> iterator) {
		HashSet<T> hs=new HashSet<T>();
		while (iterator.hasNext()) {
			hs.add(iterator.next());
		}
		return hs;
	}
	
	/**
	 * Hashcode for an int, defined as the value of the int itself for consistency with java.lang.Integer
	 * 
	 * @param value
	 * @return
	 */
	public static int hashCode(int value) {
		return value;
	}
	
	public static int hashCode(Object value) {
		if (value==null) return 0;
		return value.hashCode();
	}
	
	/** 
	 * Hashcode for a double primitive
	 * 
	 * @param d
	 * @return
	 */
	public static int hashCode(double d) {
		return hashCode(Double.doubleToLongBits(d));
	}
	
	/**
	 * Hashcode for a long primitive
	 * @param l
	 * @return
	 */
	public static int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}
	
	/**
	 * Re-throws an exception in unchecked form, without wrapping in a new
	 * exception. Useful for avoiding the need to declare unchecked exceptions.
	 * 
	 * @param t
	 */
	public static void sneakyThrow(Throwable t) {
		sneakyRethrow(t);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void sneakyRethrow(Throwable t) throws T {
		throw (T) t;
	}
}
