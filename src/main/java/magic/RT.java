package magic;

/**
 * Static class to support the Clojure runtime
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

}
