package magic.fn;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestFunctions {

	@Test public void testMultiFn() {
		AFn<?> f1=new AFn1<Object, Object>() {
			@Override
			public Object apply(Object a) {
				return "1";
			}
		};
		
		AFn<?> f2=new AFixedFn<Object>(2) {

			@Override
			public int arity() {
				return 2;
			}

			@Override
			public Object applyToArray(Object... a) {
				return "2";
			}
		};
		
		MultiFn<?> mf=MultiFn.create(f1,f2);
		
		assertEquals("1",mf.apply(3));
		assertEquals("2",mf.apply(2,3));
		
	}
}
