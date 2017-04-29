package magic.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import magic.data.IPersistentList;

public class TestParser {

	@Test public void testString() {
		Object c=Parser.parse("\"foo\"");
		assertEquals("foo",c);
	}
	
	@Test public void testDouble() {
		Object c=Parser.parse("-3.0");
		assertEquals(Double.valueOf(-3.0),c);
	}
	
	@Test public void testLong() {
		Object c=Parser.parse("-3");
		assertEquals(Long.valueOf(-3),c);
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
}
