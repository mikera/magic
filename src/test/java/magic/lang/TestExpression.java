package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.expression.Constant;

public class TestExpression {
	/**
	 * An empty Context for texting purposes
	 */
	static final Context ec=new Context();
	
	@Test public void testConstant() {
		Constant c=new Constant("Foo");
		assertEquals("Foo",ec.evaluate(c));
	}
}
