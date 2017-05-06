package magic;

import java.util.HashSet;
import java.util.Iterator;

import magic.ast.Constant;
import magic.compiler.Expanders;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * Static class to support the Magic runtime
 * 
 * @author Mike
 */
public class RT {

	public static final Context INITIAL_CONTEXT = createInitialContext();

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
	 * Sets up the initial Magic context for language bootstrap
	 * @return
	 */
	private static Context createInitialContext() {
		Context c=Context.EMPTY;
		
		// c=c.define(Symbols.QUOTE, Constant.create(Expanders.QUOTE));
		
		return c;
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
	 * @param t
	 * @param t2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> int compare(T t, T t2) {
		if (t==t2) return 0;
		if (t==null) return -1;
		if (t2==null) return 1;
		return ((Comparable<? super T>)t).compareTo(t2);
	}
	
	/**
	 * Creates a HashSet containing the values from the given iterator. Removes duplicates.
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
	
	public static <T> int iteratorHashCode(Iterator<T> data) {
		int result=0;
		
		while(data.hasNext()) {
			result^=hashCode(data.next());
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}
	
	public static <T> int arrayHashCode(T[] data, int offset, int size) {
		int result=0;
		
		for (int i=0; i<size; i++) {
			result^=hashCode(data[offset+i]);
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}
	

	public static <T> int arrayHashCode(T[] data) {
		return arrayHashCode(data,0,data.length);
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

	public static String print(Object value) {
		if (value instanceof String) {
			return "\""+value.toString()+"\"";
		} 
		return value.toString();
	}

	public static int hashCombine(int hash1, int hash2) {
		return (hash1)+(Integer.rotateLeft(hash2, 13));
	}

	public static String toString(Object o) {
		if (o==null) return "nil";
		return o.toString();
	}

}
