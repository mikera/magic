package magic;

public class RT {

	public static boolean bool(Object o) {
		if (o instanceof Boolean) {
			return ((Boolean)o);
		}
		return (o!=null);
	}

}
