package magic.lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public final class Tools {

	public static class HashComparator<T> implements Comparator<T>, Serializable {
		private static final long serialVersionUID = -568440287836864164L;

		public int compare(T o1, T o2) {
			return o2.hashCode() - o1.hashCode();
		}
	}

	public static class DefaultComparator<T> implements Comparator<T>, Serializable {
		private static final long serialVersionUID = 1695713461396657889L;

		@SuppressWarnings("unchecked")
		public int compare(T o1, T o2) {
			return ((Comparable<T>) o1).compareTo(o2);
		}
	}

	public static void debugBreak(Object o) {
		o.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compareWithNulls(Object a, Object b) {
		if (a == b) {
			return 0;
		}
		if ((a == null))
			return -1;
		if ((b == null))
			return 1;
		return ((Comparable) a).compareTo(b);
	}

	public static final boolean equalsWithNulls(Object a, Object b) {
		if (a == b)
			return true;
		if ((a == null) || (b == null))
			return false;
		return a.equals(b);
	}

	/**
	 * Hash code based on summed hash codes of individual integer values
	 * 
	 * Defined as XOR of hashcodes of all elements rotated right for each
	 * element, to be consistent with PersistentList<T>
	 * 
	 * @param data
	 * @return
	 */
	public static int hashCode(int[] data) {
		int result = 0;
		for (int i = 0; i < data.length; i++) {
			result ^= hashCode(data[i]);
			result = Integer.rotateRight(result, 1);
		}
		return result;
	}

	public static <T> int hashCode(T[] data) {
		int result = 0;
		for (int i = 0; i < data.length; i++) {
			result ^= data[i].hashCode();
			result = Integer.rotateRight(result, 1);
		}
		return result;
	}

	public static <T> int hashCode(Iterator<T> data) {
		int result = 0;

		while (data.hasNext()) {
			result ^= hashCode(data.next());
			result = Integer.rotateRight(result, 1);
		}
		return result;
	}

	/**
	 * Hashcode for an int, defined as the value of the int itself for
	 * consistency with java.lang.Integer
	 * 
	 * @param value
	 * @return
	 */
	public static int hashCode(int value) {
		return value;
	}
	
	/**
	 * Computes a hash code, allowing for null values to hash to zero.
	 * 
	 * @param value
	 * @return
	 */
	public static int hashCode(Object value) {
		if (value == null)
			return 0;
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
	 * 
	 * @param l
	 * @return
	 */
	public static int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}

	/**
	 * Compares two Comparable values, considering null as the lowest possible
	 * value
	 * 
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T extends Comparable<? super T>> int compareWithNulls(T a, T b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}

	/**
	 * Re-throws an exception in unchecked form, without wrapping in a new
	 * exception. Useful for avoiding the need to declare unchecked exceptions.
	 * 
	 * @param t
	 */
	public static void sneakyThrow(Throwable t) {
		Tools.<RuntimeException> sneakyRethrow(t);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void sneakyRethrow(Throwable t) throws T {
		throw (T) t;
	}
}
