package magic.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import magic.Core;
import magic.RT;
import magic.Symbols;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.fn.AFn;

public class TestCoreFns {

	@SuppressWarnings("unchecked")
	private <T> T exec(String code) {
		return (T) Core.eval(code).getValue();
	}
	
	@Test public void testEmptyEval() {
		assertNull(exec(""));
		assertNull(exec("  "));
	}
	
	
	@Test public void testComment() {
		assertNull(exec("(comment ignored-symbol)"));
		assertNull(exec("(comment ignored-symbol :foo)"));
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
	
	@Test public void testNext() {
		assertEquals(Tuple.of(2L,3L),exec("(vec (next [1 2 3]))"));
		assertNull(exec("(next [1])"));
		 
		try {
			exec("(next nil)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		} 
		
		try {
			exec("(next [])");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		} 
	}
	
	@Test public void testBooleanQ() {
		assertTrue(exec("(boolean? true)"));
		assertTrue(exec("(boolean? false)"));
		assertFalse(exec("(boolean? nil)"));
		assertTrue(exec("(boolean? (= 2 3))"));
	}
	
	@Test public void testNumberQ() {
		assertTrue(exec("(number? 1)"));
		assertTrue(exec("(number? 2.0)"));
		assertFalse(exec("(number? nil)"));
		assertFalse(exec("(number? :foo)"));
	}
	
	@Test public void testConcat() {
		assertEquals(Tuple.of(1L,2L,3L,4L),exec("(concat [1 2] [3 4])"));
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
		
		try {
			exec("(cast java.lang.String 1)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
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
		assertEquals("(1 2)",exec("(str (seq [1 2]))"));
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
	
	@Test public void testNot() {
		assertTrue(RT.bool(exec("(not false)")));
		Object o2=exec("(not 1)");
		assertFalse(RT.bool(o2));
	}
	
	@Test public void testSymbol() {
		AFn<?> fn=(AFn<?>)Core.INITIAL_CONTEXT.getValue(Symbols.SYMBOL);
		// System.out.println(fn);
		assertTrue(fn.hasArity(2));
		assertEquals(Symbol.create("foo","bar"),exec("(symbol 'foo 'bar)"));
		assertEquals(Symbol.create("foo","bar"),exec("(symbol \"foo\" \"bar\")"));
		assertEquals(Symbol.create("bar"),exec("(symbol nil, 'bar)"));
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
	
	@Test public void testApply() {
		assertEquals((Long)3L,exec("(apply + [1 2])"));
		assertEquals((Long)3L,exec("(apply + 1 [2])"));
		assertTrue(exec("(apply = 1 1 [1 1 1])"));
		assertFalse(exec("(apply = 1 2 [1 2 1])"));
	}
	
	@Test public void testIdentical() {
		assertTrue(RT.bool(exec("(identical? nil nil)")));
		assertTrue(RT.bool(exec("(let [a 1] (identical? a a))")));
		assertEquals(Boolean.FALSE,exec("(identical? 1 2)")); // TODO: why is this broken?
	}
	
	@Test public void testIncDec() {
		assertEquals((Long)1L,exec("(inc(dec 1))"));
		assertEquals((Long)4L,exec("(inc 3)"));
		assertEquals((Long)2L,exec("(dec 3)"));
		
		try {
			exec("(inc :foo)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testPlus() {
		assertEquals((Double)2.0,exec("(+ 1 1.0)"));
		assertEquals((Double)3.0,exec("(+ 2.0 1.0)"));
		assertEquals((Long)4L,exec("(+ 1 3)"));
		
		try {
			exec("(+ 1 :foo)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testMinus() {
		assertEquals((Double)1.0,exec("(- 2 1.0)"));
		assertEquals((Double)3.0,exec("(- 4.0 1.0)"));
		assertEquals((Long)4L,exec("(- 6 2)"));
		
		try {
			exec("(- 1 :foo)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testMul() {
		assertEquals((Double)2.0,exec("(* 2 1.0)"));
		assertEquals((Double)3.0,exec("(* 6.0 0.5)"));
		assertEquals((Long)6L,exec("(* 2 3)"));
		
		try {
			exec("(* 1 :foo)");
			fail("Should fail!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testComps() {
		assertTrue(exec("(> 2 1.0)"));
		assertFalse(exec("(< 2 1.0)"));
		assertFalse(exec("(<= 2 -10)"));
		assertTrue(exec("(== 2 2.0)"));
		assertFalse(exec("(== 2 1)"));
	
	}
	
//	@Test public void testWhen() {
//		assertEquals("foo",exec("(when true (str 'foo))"));
//		assertNull(exec("(when false (str 'foo))"));
//
//		
//	}



}
