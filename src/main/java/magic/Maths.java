package magic;

public class Maths {

	public static int min(int a, int b) {
		return (a<b)?a:b;
	}
	
	public static int max(int a, int b) {
		return (a>b)?a:b;
	}
	
	public static long min(long a, long b) {
		return (a<b)?a:b;
	}
	
	public static long max(long a, long b) {
		return (a>b)?a:b;
	}
	
	public static long add(int a, int b) {
		return (a>b)?a:b;
	}
	
	public static Number add(Object a, Object b) {
		if ((a instanceof Long)&&(b instanceof Long)) {
			return add((Long)a,(Long)b);
		}
		return ((Number)a).doubleValue()+((Number)b).doubleValue();
	}
	
	public static Long add(Long a, Long b) {
		return a+b;
	}
	
	public static Number sub(Object a, Object b) {
		if ((a instanceof Long)&&(b instanceof Long)) {
			return sub((Long)a,(Long)b);
		}
		return ((Number)a).doubleValue()-((Number)b).doubleValue();
	}
	
	public static Long sub(Long a, Long b) {
		return a-b;
	}
	
	public static Number mul(Object a, Object b) {
		if ((a instanceof Long)&&(b instanceof Long)) {
			return mul((Long)a,(Long)b);
		}
		return ((Number)a).doubleValue()*((Number)b).doubleValue();
	}
	
	public static Long mul(Long a, Long b) {
		return a*b;
	}

}
