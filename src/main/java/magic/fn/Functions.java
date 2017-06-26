package magic.fn;

/**
 * Some Magic functions defined in Java
 * 
 * Experimental... should probably be in pure Magic?
 * 
 * @author Mike
 *
 */
public class Functions {
	public static final AFn<Object> PRINTLN = new AVariadicFn<Object>(){
		@Override
		public Object applyToArray(Object... a) {
			for (int i=0; i<a.length; i++) {
				System.out.print(a[i].toString());
			}
			System.out.println("\n");
			return null;
		}
	};

}
