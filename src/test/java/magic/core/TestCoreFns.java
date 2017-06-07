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
	
	@Test public void testStr() {
		assertEquals(":foo",exec("(str :foo)"));
		assertEquals("1",exec("(str 1)"));
		assertEquals("[1 2]",exec("(str [1 2])"));
	}
	
	@Test public void testInstanceOf() {
		assertTrue(RT.bool(exec("(instance? java.lang.String (str :foo))")));
		assertTrue(RT.bool(exec("(instance? java.lang.Long 1)")));
		assertFalse(RT.bool(exec("(instance? java.lang.Long nil)")));
		
	}
	
	@Test public void testIdentity() {
		assertEquals((Long)1L,exec("(identity 1)"));
		assertNull(exec("(identity nil)"));
		assertEquals("foo",exec("(identity (str 'foo))"));
		
	}
	
	@Test public void testBoolean() {
		assertTrue(RT.bool(exec("(boolean 1)")));
		assertTrue(RT.bool(exec("(boolean :foo)")));
		assertTrue(RT.bool(exec("(boolean true)")));
		assertEquals(Boolean.FALSE,exec("(boolean nil)"));
		assertFalse(RT.bool(false));
		Object o=exec("(boolean false)");
		Object o2=Boolean.FALSE;
		assertFalse(RT.bool(o2));
		assertFalse(RT.bool(o));
		
	}



}
