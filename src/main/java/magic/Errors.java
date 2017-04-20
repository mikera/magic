package magic;

public class Errors {

	public static String negativeRange() {
		return "Negative range specified";
	}

	public static String rangeOutOfBounds(int start, int end) {
		return "Range out of bounds ["+start+","+end+")";
	}

	public static String indexOutOfBounds(int i) {
		// TODO Auto-generated method stub
		return "Index out of bounds: "+i;
	}

}
