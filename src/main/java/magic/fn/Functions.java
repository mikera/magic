package magic.fn;

import magic.RT;

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

		@Override
		public int arity() {
			return 0;
		}
	};
	
	public static final AFn<Long> LONGADD = new AVariadicFn<Long>(){
		@Override
		public Long applyToArray(Object... a) {
			long r=0;
			for (int i=0; i<a.length; i++) {
				r+=RT.longValue(a[i]);
			}
			return r;
		}

		@Override
		public int arity() {
			return 0;
		}
	};

}
