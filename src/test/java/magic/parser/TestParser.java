package magic.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import magic.data.IPersistentList;
import magic.data.Maps;
import magic.data.Sets;
import magic.data.Symbol;

public class TestParser {

	@Test public void testString() {
		Object c=Parser.parse("\"foo\"");
		assertEquals("foo",c);
	}
	
	@Test public void testDouble() {
		assertEquals(Double.valueOf(-3.0),Parser.parse("-3.0"));
		assertEquals(Double.valueOf(.3e10),Parser.parse(".3e10"));
		assertEquals(Double.valueOf(-.3e10),Parser.parse("-0.3e10"));
	}
	
	@Test public void testLong() {
		assertEquals(Long.valueOf(-3),Parser.parse("-3"));
		assertEquals(Long.valueOf(3000),Parser.parse("+3000"));
		assertEquals(Long.valueOf(-21),Parser.parse("-21"));
	}
	
	@Test public void testQualifiedSymbol() {
		assertEquals(Symbol.create("foo"),Parser.parse("foo"));

		assertEquals(Symbol.create("/"),Parser.parse("/"));
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testVector() {
		Object c=Parser.parse("[-3 -1.0 \"foo\"]");
		IPersistentList<Object> exps=(IPersistentList<Object>) c;
		assertEquals(3,exps.size());
		assertEquals(Long.valueOf(-3),exps.get(0));
		assertEquals(Double.valueOf(-1.0),exps.get(1));
		assertEquals("foo",exps.get(2));
	}
	
	@Test public void testSet() {
		Object c=Parser.parse("#{3}");
		assertEquals(Sets.of(3L),c);
	}
	
	@Test public void testSetDuplicates() {
		Object c=Parser.parse("#{foo foo}");
		assertEquals(Sets.of(Symbol.create("foo")),c);
	}
	
	@Test public void testMap() {
		Object c=Parser.parse("{1 2}");
		assertEquals(Maps.create(1L, 2L),c);
	}
}
