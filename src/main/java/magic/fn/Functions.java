package magic.fn;

public class Functions {

	public static final AFn<Object> PRINTLN = new AFn<Object>(){

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
