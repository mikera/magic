package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPredicates {

	@Test public void testPredicate() {
		java.util.function.Predicate<Long> odd = (Long d) -> ((d&1L)==1L);
		
		assertEquals(true,odd.test(3L));
		assertEquals(false,odd.test(4L));
	}
}
