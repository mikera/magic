package magic.core;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.data.Tuple;

public class TestCoreFns {

	@SuppressWarnings("unchecked")
	private <T> T exec(String code) {
		return (T) RT.compile(code).getValue();
	}
	
	@Test public void testComment() {
		Object v=exec("(comment ignored-symbol)");
		assertNull(v);
	}
	
	@Test public void testVec() {
		Object v=exec("(def a '[1 2 3]) "
				    + "(vec a)");
		assertEquals(Tuple.of(1L,2L,3L),v);
		
	}


}
