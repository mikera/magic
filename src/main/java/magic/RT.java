package magic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import magic.compiler.EvalResult;
import magic.ast.Constant;
import magic.ast.Node;
import magic.compiler.Expanders;
import magic.data.IPersistentObject;
import magic.data.Symbol;
import magic.fn.Functions;
import magic.lang.Context;
import magic.lang.Symbols;
import magic.type.JavaType;

/**
 * Static class to support the Magic runtime
 * 
 * @author Mike
 */
public class RT {

	public static final Context BOOTSTRAP_CONTEXT = createBootstrapContext();
	public static final Context INITIAL_CONTEXT = createInitialContext();
	public static final Symbol[] EMPTY_SYMBOLS = new Symbol[0];
	public static final Node<?>[] EMPTY_NODES = new Node<?>[0];

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
	private static Context createBootstrapContext() {
		Context c=Context.EMPTY;
		c=c.define(Symbols.DEFN, Constant.create(Expanders.DEFN));
		c=c.define(Symbols.DEFMACRO, Constant.create(Expanders.DEFMACRO));
		c=c.define(Symbols.MACRO, Constant.create(Expanders.MACRO));
		c=c.define(Symbols.QUOTE, Constant.create(Expanders.SPECIAL_FORM));
		c=c.define(Symbols.SYNTAX_QUOTE, Constant.create(Expanders.SPECIAL_FORM));
		c=c.define(Symbols.PRINTLN, Constant.create(Functions.PRINTLN)); 
		return c;
	}
	
	/**
	 * Loads magic.core to create the initial user context
	 * @return
	 * @throws FileNotFoundException 
	 */
	private static Context createInitialContext() {
		Context c=BOOTSTRAP_CONTEXT;
		EvalResult<?> r;
		try {
			r=(EvalResult<?>) magic.compiler.Compiler.compile(c, RT.getResourceAsString("magic/core.mag"));
		} catch (FileNotFoundException e) {
			throw new Error(e);
		}
		return r.getContext();
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
		return RT.toString(value);
	}

	public static int hashCombine(int hash1, int hash2) {
		return (hash1)+(Integer.rotateLeft(hash2, 13));
	}

	public static String toString(Object o) {
		if (o==null) return "nil";
		if (o instanceof String) return "\""+(String)o+"\"";
		return o.toString();
	}
	
	public static URL getResourceURL(String filename) {
		return Thread.currentThread().getContextClassLoader().getResource(filename);
	}
	
	public static InputStream getResourceAsStream(String filename) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	}
	
	public static String getResourceAsString(String filename) throws FileNotFoundException {
		InputStream is=getResourceAsStream(filename);
		if (is==null) throw new FileNotFoundException(filename);
		return readStringFromStream(is);
	}
	
	public static String readStringFromStream(InputStream stream) {
		return readStringFromStream(stream,Charset.defaultCharset());
	}
	
	public static ArrayList<String> readStringLinesFromStream(InputStream stream) {
		ArrayList<String> al=new ArrayList<String>();
		BufferedReader reader=null;
		try {
			try {
				reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()));
				String s;
				while ((s=reader.readLine())!=null) {
					al.add(s);
				}
			} finally {
		        if (reader!=null) reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }  
		
		return al;
	}
	
	public static String readStringFromStream(InputStream stream, Charset cs) {
		try {
		    Reader reader=null;
			try {
		        reader = new BufferedReader(new InputStreamReader(stream, cs));
		        StringBuilder builder = new StringBuilder();
		        char[] buffer = new char[4096];
		        int readBytes;
		        while ((readBytes = reader.read(buffer, 0, buffer.length)) > 0) {
		            builder.append(buffer, 0, readBytes);
		        }
		        return builder.toString();
		    } finally {
		        if (reader!=null) reader.close();
		    }   
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getCommonSuperclass(Type[] types) {
		Class<?> c=types[0].getJavaClass();
		for (int i=1; i<types.length; i++) {
			Class<?> ci=types[i].getJavaClass();
			while (!c.isAssignableFrom(ci)) {
				c=c.getSuperclass();
			}
		}
		return (Class<T>) c;
	}

	public static Type inferType(Object value) {
		if (value==null) return Types.NULL;
		// TODO: function type inference
//		if (value instanceof IFn) {
//			return ((IFn)value).getType();
//		}
		if (value instanceof IPersistentObject) {
			return ((IPersistentObject)value).getType();
		}
		return (Type) JavaType.create(value);
	}

}
