package magic.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.expression.Constant;

public class TestParser {

	@SuppressWarnings("unchecked")
	@Test public void testString() {
		Constant<String> c=(Constant<String>) MagicParser.parseExpression("\"foo\"");
		assertEquals("foo",c.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testDouble() {
		Constant<Double> c=(Constant<Double>) MagicParser.parseExpression("-3.0");
		assertEquals(Double.valueOf(-3.0),c.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testLong() {
		Constant<Long> c=(Constant<Long>) MagicParser.parseExpression("-3");
		assertEquals(Long.valueOf(-3),c.getValue());
	}
}
