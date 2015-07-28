package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.fn.AFn1;
import java.util.function.Predicate;

public class TestPredicates {

	@Test public void testPredicate() {
		java.util.function.Predicate<Long> odd = (Long v) -> ((v&1L)==1L);
		
		assertEquals(true,odd.test(3L));
		assertEquals(false,odd.test(4L));
	}
	
	@Test public void testAFn1() {
		Predicate<Object> odd = new AFn1<Object,Boolean>() {
			@Override
			public Boolean apply(Object a) {
				Long v=(Long)a;
				return ((v&1L)==1L);
			}	
		};
		
		assertEquals(true,odd.test(3L));
		assertEquals(false,odd.test(4L));
	}
}
