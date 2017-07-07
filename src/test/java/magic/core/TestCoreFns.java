package magic.core;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.Core;
import magic.RT;
import magic.data.Tuple;

public class TestCoreFns {

	@SuppressWarnings("unchecked")
	private <T> T exec(String code) {
		return (T) Core.compile(code).getValue();
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
	
	@Test public void testEmptySeq() {
		Object v=exec("(def a []) "
				    + "(seq a)");
		assertNull(v);
	}
	
	@Test public void testVecSeq() {
		Object v=exec("(vec (seq [1 2 3]))");
		assertEquals(Tuple.of(1L,2L,3L),v);
	}
	
	@Test public void testNth() {
		Object v=exec("(nth [1 2 3] 1)");
		assertEquals(2L,v);
		
		try {
			exec("(nth [1 2 3] -1)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
		
		try {
			exec("(nth [1 2 3] 3)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testFirst() {
		Object v=exec("(first [1 2 3])");
		assertEquals(1L,v);
		
		try {
			exec("(first [])");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testCast() {
		Object v=exec("(cast java.lang.String \"foo\")");
		assertEquals("foo",v);
	}
	
	@Test public void testEquals() {
		assertTrue(exec("(= :foo :foo)"));
		assertTrue(exec("(= 1 1)"));
		assertTrue(exec("(= nil nil)"));
		assertTrue(exec("(= [] [])"));
		assertFalse(exec("(= nil 1)"));
		assertFalse(exec("(= 1 nil)"));
		assertFalse(exec("(= nil 1)"));
		assertFalse(exec("(= 1 1 2)"));
		assertFalse(exec("(= 1 1 nil)"));
		assertFalse(exec("(= :foo :bar)"));
	}
	
	@Test public void testStr() {
		assertEquals(":foo",exec("(str :foo)"));
		assertEquals("1",exec("(str 1)"));
		assertEquals("nil",exec("(str nil)"));
		assertEquals("[1 2]",exec("(str [1 2])"));
	}
	
	@Test public void testInstanceOf() {
		assertTrue(RT.bool(exec("(instance? java.lang.String (str :foo))")));
		assertTrue(RT.bool(exec("(instance? java.lang.Long 1)")));
		
		assertFalse(RT.bool(exec("(instance? java.lang.Long nil)")));
		assertFalse(RT.bool(exec("(instance? java.lang.Long :foo)")));
		
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
	
	@Test public void testNilQ() {
		assertTrue(RT.bool(exec("(nil? nil)")));
		assertFalse(RT.bool(exec("(nil? 1)")));
		assertFalse(RT.bool(exec("(nil? [])")));
	}
	
	@Test public void testVectorQ() {
		assertFalse(exec("(vector? nil)"));
		assertTrue(exec("(vector? [1])"));
		assertTrue(exec("(vector? [])"));
	}
	
	@Test public void testIdentical() {
		assertTrue(RT.bool(exec("(identical? nil nil)")));
		assertTrue(RT.bool(exec("(let [a 1] (identical? a a))")));
		assertEquals(Boolean.FALSE,exec("(identical? 1 2)")); // TODO: why is this broken?
	}
	
//	@Test public void testWhen() {
//		assertEquals("foo",exec("(when true (str 'foo))"));
//		assertNull(exec("(when false (str 'foo))"));
//
//		
//	}



}
