package magic.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.data.IPersistentList;
import magic.expression.Constant;
import magic.expression.Expression;
import magic.expression.LongConstant;
import magic.expression.Vector;

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
	
	@Test public void testLong() {
		LongConstant c=(LongConstant) MagicParser.parseExpression("-3");
		assertEquals(Long.valueOf(-3),c.getValue());
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testVector() {
		Vector<Object> c=(Vector<Object>) MagicParser.parseExpression("[-3 -1.0 \"foo\"]");
		IPersistentList<Expression<Object>> exps=c.getExpressions();
		assertEquals(3,exps.size());
		assertEquals(Long.valueOf(-3),exps.get(0).getValue());
		assertEquals(Double.valueOf(-1.0),exps.get(1).getValue());
		assertEquals("foo",exps.get(2).getValue());
	}
}
