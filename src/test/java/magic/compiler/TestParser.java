package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.compiler.Reader;
import magic.data.IPersistentList;
import magic.data.IPersistentVector;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.Sets;
import magic.data.Symbol;

public class TestParser {

	@Test public void testString() {
		Object c=Reader.read("\"foo\"");
		assertEquals("foo",c);
	}
	
	@Test public void testDouble() {
		assertEquals(Double.valueOf(-3.0),Reader.read("-3.0"));
		assertEquals(Double.valueOf(.3e10),Reader.read(".3e10"));
		assertEquals(Double.valueOf(-.3e10),Reader.read("-0.3e10"));
	}
	
	@Test public void testNil() {
		assertNull(Reader.read("nil"));
	}
	
	@Test public void testKeywords() {
		assertEquals(Keyword.class,Reader.read(":foo").getClass());
	}
	
	@Test public void testBooleans() {
		assertTrue((Boolean)Reader.read("true"));
		assertFalse((Boolean)Reader.read("false"));
		assertTrue(RT.bool(Reader.read("true")));
		assertFalse(RT.bool(Reader.read("false")));
	}

	
	@Test public void testLong() {
		assertEquals(Long.valueOf(-3),Reader.read("-3"));
		assertEquals(Long.valueOf(3000),Reader.read("+3000"));
		assertEquals(Long.valueOf(-21),Reader.read("-21"));
	}
	
	@Test public void testQualifiedSymbol() {
		assertEquals(Symbol.create("foo","bar"),Reader.read("foo/bar"));
		assertEquals("foo",((Symbol)Reader.read("foo//")).getNamespace());
		assertEquals(Symbol.create("foo","/"),Reader.read("foo//"));
	}
	
	@Test public void testUnQualifiedSymbol() {
		assertEquals(Symbol.create("foo"),Reader.read("foo"));
		assertEquals(Symbol.create("/"),Reader.read("/"));
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testVector() {
		Object c=Reader.read("[-3 -1.0 \"foo\"]");
		IPersistentVector<Object> exps=(IPersistentVector<Object>) c;
		assertEquals(3,exps.size());
		assertEquals(Long.valueOf(-3),exps.get(0));
		assertEquals(Double.valueOf(-1.0),exps.get(1));
		assertEquals("foo",exps.get(2));
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testList() {
		Object c=Reader.read("(-3 -1.0 \"foo\")");
		IPersistentList<Object> exps=(IPersistentList<Object>) c;
		assertEquals(3,exps.size());
		assertEquals(Long.valueOf(-3),exps.get(0));
		assertEquals(Double.valueOf(-1.0),exps.get(1));
		assertEquals("foo",exps.get(2));
	}
	
	@Test public void testSet() {
		assertEquals(Sets.of(3L),Reader.read("#{3}"));
		assertEquals(Sets.of(1L,2L,3L),Reader.read("#{1,3,2,3,1}"));
	}
	
	@Test public void testSetDuplicates() {
		Object c=Reader.read("#{foo foo}");
		assertEquals(Sets.of(Symbol.create("foo")),c);
	}
	
	@Test public void testMap() {
		Object c=Reader.read("{1 2}");
		assertEquals(Maps.create(1L, 2L),c);
	}
	
	@Test public void testQuote() {
		assertEquals(Reader.read("(quote foo/bar)"),Reader.read("'foo/bar"));
		assertEquals(Reader.read("(unquote (quote foo))"),Reader.read("~'foo"));
		assertEquals(Reader.read("(unquote (syntax-quote foo))"),Reader.read("~`foo"));
	}
	
	@Test public void testExtraInputFail() {
		try {
		  Reader.read("{1 2 3} [1]");
		  fail("Shouldn't be able to parse multiple expressions!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testMapFail() {
		try {
		  Reader.read("{1 2 3}");
		  fail("Shouldn't be able to create this map!");
		} catch (Throwable t) {
			//System.out.println(t.getMessage());
			// OK
		}
	}
}
