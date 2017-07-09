package magic;

/**
 * Static class for constructing error messages
 * @author Mike
 *
 */
public class Errors {

	public static String negativeRange() {
		return "Negative range specified";
	}

	public static String rangeOutOfBounds(int start, int end) {
		return "Range out of bounds ["+start+","+end+")";
	}

	public static String indexOutOfBounds(int i) {
		return "Index out of bounds: "+i;
	}

	public static String immutable(Object o) {
		return "Class is immutable: "+RT.className(o);
	}

	public static String metaNotSupported(Object o) {
		return "Metadata is not supported on objects of class: "+RT.className(o);
	}

}
