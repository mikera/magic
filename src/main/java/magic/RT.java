package magic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import magic.ast.Node;
import magic.data.APersistentCollection;
import magic.data.APersistentList;
import magic.data.APersistentSequence;
import magic.data.APersistentVector;
import magic.data.IPersistentObject;
import magic.data.ISeq;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.fn.IFn;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.UnresolvedException;
import magic.type.JavaType;
import magic.type.TypeError;
import magic.type.Value;

/**
 * Static class of utility functions to support the Magic runtime implementation
 * 
 * @author Mike
 */
public class RT {
	
	private static final String identifierPattern="\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
	private static final Pattern classNameRegex=Pattern.compile(identifierPattern + "(\\." + identifierPattern + ")*");
	
	/** 
	 * Returns true if a String could be a valid Java class name
	 * @param s
	 * @return
	 */
	public static boolean maybeClassName(String s) {
		return classNameRegex.matcher(s).matches();
	}

	public static final Symbol[] EMPTY_SYMBOLS = new Symbol[0];

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
	 * Computes the negation of an object's truthiness
	 * i.e. nil or Boolean.FALSE returns true, everything else returns false
	 * @param o
	 * @return
	 */
	public static Boolean not(Object o) {
		if (o == Boolean.FALSE) return Boolean.TRUE;
		return (o==null)?Boolean.TRUE:Boolean.FALSE;
	}
	
	/**
	 * Converts an object to a Boolean Object value, according to Clojure's truthiness rules
	 * i.e. nil or Boolean.FALSE is false, everything else is truthy
	 * @param o
	 * @return
	 */
	public static Boolean boolObject(Object o) {
		if (o == Boolean.FALSE) return Boolean.FALSE;
		return (o!=null)?Boolean.TRUE:Boolean.FALSE;
	}
	
	/**
	 * Converts an boolean to a Boolean value
	 * i.e. nil or Boolean.FALSE is false, everything else is truthy
	 * @param o
	 * @return
	 */
	public static Boolean boolObject(boolean o) {
		return o?Boolean.TRUE:Boolean.FALSE;
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
	
	public static final Boolean equalsObject(Object a, Object b) {
		if (a==b) return Boolean.TRUE;
		if ((a==null)||(b==null)) return Boolean.FALSE;
		return a.equals(b)?Boolean.TRUE:Boolean.FALSE;
	}
	
	/**
	 * Returns true if and only if two values are identical.
	 * Handles nulls as distinct values that are identical to null only.
	 * 
	 * @param a
	 * @param b
	 * @return Boolean.FALSE or Boolean.TRUE
	 */
	public static final Boolean identical(Object a, Object b) {
		return a==b;
	}
	
	/**
	 * Returns a symbol with the given name
	 * @param name
	 * @return
	 */
	public static Symbol symbol(Object name) {
		return symbol(null,name);
	}
	
	/**
	 * Returns a symbol with the given namespace and name
	 * @param name
	 * @return
	 */
	public static Symbol symbol(Object namespace, Object name) {
		return Symbol.create((namespace==null)?null:namespace.toString(), name.toString());
	}
	
	/**
	 * Gets the item from an indexed collection at the given index
	 * 
	 * @param a
	 * @param index
	 * @return Boolean.FALSE or Boolean.TRUE
	 */
	public static final Object nth(Object a, Object index) {
		int i=RT.intValue(index);
		return nth(a,i);
	}
	
	public static final Object nth(Object a, int i) {
		if (a instanceof List) {
			return ((List<?>)a).get(i);
		}
		Class<?> klass=a.getClass();
		if (klass.isArray()) {
			return Array.get(a, i);
		}
		throw new IllegalArgumentException("Can't do indexed get on object of type: "+a.getClass());
	}
	
	public static final Object first(Object a) {
		return nth(a,0);
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
	 * Coerces any sequential object to a vector
	 * @param o
	 * @return
	 */
	public static APersistentVector<?> vec(Object o) {
		return Vectors.coerce(o);
	}
	
	/**
	 * Coerces any sequential object to a list
	 * @param o
	 * @return
	 */
	public static APersistentList<?> list(Object o) {
		return PersistentList.wrap(Vectors.coerce(o));
	}
	
	/**
	 * Coerces any sequential object to a ISeq.
	 * 
	 * Returns nil if the sequence is empty.
	 * @param o
	 * @return
	 */
	public static ISeq<?> seq(Object o) {
		if (o instanceof APersistentCollection) {
			return ((APersistentCollection<?>)o).seq();
		};
		return vec(o).seq();
	}
	
	@SuppressWarnings("unchecked")
	public static Object concat(Object a, Object b) {
		if (a instanceof APersistentVector) {
			return ((APersistentVector<Object>)a).concat((APersistentVector<Object>) vec(b));
		};
		throw new IllegalArgumentException("Can't concat objects of type "+a.getClass()+" and "+b.getClass());
	}
	
	/**
	 * Returns the items in the sequence after the first, as a seq
	 * 
	 * Returns nil if the sequence is empty.
	 * @param o
	 * @return
	 */
	public static ISeq<?>next(Object o) {
		if (o instanceof APersistentCollection) {
			ISeq<?> seq=((APersistentCollection<?>)o).seq();
			if (seq==null) throw new Error("Can't calculate 'next' on empty sequence");
			return seq.next();
		};
		return vec(o).seq().next();
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
		if (o instanceof Class<?>) return ((Class<?>)o).getCanonicalName();
		Class<?> klass=o.getClass();
		if (klass.isArray()) {
			return arrayToString(o);
		}
		return o.toString();
	}
	

	public static String printTypes(Object[] a) {
		StringBuilder sb=new StringBuilder("[");
		int n=a.length;
		for (int i=0; i<n; i++) {
			Object item=Array.get(a, i);
			sb.append(RT.toString(item));
			if (i<(n-1)) sb.append(", ");
		}		
		sb.append(']');
		return sb.toString();
	}
	
	public static String arrayToString(Object o, String separator) {
		StringBuilder sb=new StringBuilder("[");
		int n=Array.getLength(o);
		for (int i=0; i<n; i++) {
			Object item=Array.get(o, i);
			sb.append(RT.className(item));
			if (i<(n-1)) sb.append(separator);
		}
		sb.append(']');
		return sb.toString();
	}
	
	public static String arrayToString(Object o) {
		return arrayToString(o,", ");
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
			throw new magic.Error("Failed to read String from stream: "+stream.toString(),t);
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

	/**
	 * Infers the default type of a value.
	 * @param value
	 * @return
	 */
	public static Type inferType(Object value) {
		if (value==null) return Types.NULL;
		// TODO: function type inference
//		if (value instanceof IFn) {
//			return ((IFn)value).getType();
//		}
		if (value instanceof IPersistentObject) {
			return ((IPersistentObject)value).getType();
		}
		return (Type) Value.create(value);
	}

	/**
	 * Converts a sequence of values to a string, using the provided separator
	 * @param values
	 * @param sep
	 * @return
	 */
	public static String toString(APersistentSequence<?> values, String separator) {
		int n=values.size();
		if (n==0) return "";
		StringBuilder sb=new StringBuilder(RT.toString(values.get(0)));
		for (int i=1; i<n; i++) {
			sb.append(separator);
			sb.append(RT.print(values.get(i)));
		}
		return sb.toString();
	}
	

	public static String toString(Object[] things, String separator) {
		int n=things.length;
		if (n==0) return "";
		StringBuilder sb=new StringBuilder(RT.toString(things[0]));
		for (int i=1; i<n; i++) {
			sb.append(separator);
			sb.append(RT.print(things[i]));
		}
		return sb.toString();
	}
	
	/**
	 * Generates a symbol with a unique ID and default prefix
	 * @return
	 */
	public static Symbol genSym() {
		return genSym("g_");
	}
	
	/**
	 * Private field for gensym IDs
	 */
	private static long gensymID=1;
	
	/**
	 * Generates a symbol with a unique ID and the given prefix
	 * @return
	 */
	public static Symbol genSym(String prefix) {
		return Symbol.create(prefix+(gensymID++));
	}

	/**
	 * Gets the class for a given name, or null if not found
	 * @param name
	 * @return
	 */
	public static Class<?> classForName(String name) {
		try {
			return Class.forName(name,false,RT.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Class<?> classForSymbol(Symbol sym) {
		String name=sym.getName();
		if (!maybeClassName(name)) return null;
		return classForName(name);
	}

	/** 
	 * Resolves a symbol in the current context, namespace qualifying if necessary
	 * @param c
	 * @param symbol
	 * @return
	 */
	public static Symbol resolveSym(Context c, Symbol symbol) {
		if (symbol.isQualified()) return symbol;
		
		// TODO: remove this check once everything is working
		if (c==null) throw new Error("Null context when trying to resove symbol: " +symbol);
			
		String ns=c.getCurrentNamespace();
		if (ns!=null) {
			symbol=Symbol.create(ns,symbol.getName());
		}
		return symbol;
	}
	
	/**
	 * Gets the value associated with a Symbol in the given context.
	 * 
	 * Handles:
	 * - Lookup of values from slots in this context
	 * - Lookup of class names
	 * - Special case of *context*
	 * 
	 * @param c
	 * @param sym
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T resolve(Context c, Symbol sym) {
		if (sym==Symbols._CONTEXT_) return (T) c;
		
		Slot<T> slot=resolveSlot(c,sym);
		if (slot!=null) return slot.getValue();
		
		Class<?> cls=RT.classForSymbol(sym);
		if (cls!=null) {
			Type type=JavaType.create(cls);
			return (T) type;
		}
		throw new UnresolvedException(sym);
	}
	
	/**
	 * Gets the slot associated with a Symbol in the given context.
	 * 
	 * Handles:
	 * - Lookup of values from slots in this context
	 * - Lookup of class names
	 * @param c
	 * @param sym
	 * @return
	 */
	public static <T> Slot<T> resolveSlot(Context c, Symbol sym) {
		sym=RT.resolveSym(c, sym);

		// TODO: remove this check once everything is working
		if (c==null) throw new Error("Null context when trying to resove slot: " +sym+ "\n"
				+ "Possibly caused by Lookup not being analysed before evaluation?");

		Slot<T> slot=c.getSlot(sym);
		return slot;
	}
	
	/**
	 * Resolves a node for a given symbol in this context
	 * 
	 * Returns null if the slot does not exist
	 * @param c
	 * @param sym
	 * @return
	 */
	public static Node<?> resolveNode(Context c, Symbol sym) {
		Slot<?> slot=RT.resolveSlot(c, sym);
		if (slot==null) return null;
		
		return  slot.getCompiledNode();
	}
	
	public static Object applyWith(Object f, Object args) {
		if (!(f instanceof IFn)) throw new IllegalArgumentException("apply requires a function as first argument but got: "+RT.className(f));
		
		IFn<?> fn=(IFn<?>)f;
		
		APersistentVector<?> avec=vec(args);
		int al=avec.size();
		
		int n;
		APersistentVector<?> rest;
		if (al>0) {
			rest=vec(avec.get(al-1));
			n=al-1+rest.size();
		} else {
			n=0;
			rest=Vectors.emptyVector();
		}
		
		Object[] as=new Object[n];
		for (int i=0; i<al-1; i++) {
			as[i]=avec.get(i);
		}
		
		for (int i=0; i<(n-(al-1)); i++) {
			as[(al-1)+i]=rest.get(i);
		}
		
		return fn.applyToArray(as);
	}

	/**
	 * Gets a string representing the class name of an Object
	 * 
	 * Intended for use mainly in generation of descriptive error messages.
	 * 
	 * @param o
	 * @return
	 */
	public static String className(Object o) {
		if (o==null) return "nil";
		return o.getClass().toString();
	}

	public static long longValue(Object a) {
		if (a instanceof Long) return (Long)a;
		if (a instanceof Number) {
			Number n=(Number)a;
			return n.longValue();
		};
		throw new TypeError("Can't cast value of type "+a.getClass()+" to long integer");
	}
	
	public static int intValue(Object a) {
		if (a instanceof Integer) return (Integer)a;
		if (a instanceof Number) {
			Number n=(Number)a;
			return n.intValue();
		};
		throw new TypeError("Can't cast value of type "+a.getClass()+" to int");
	}







}
