package magic.analyser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import magic.data.Tuple;
import magic.expression.Constant;
import magic.expression.Expression;
import magic.lang.Context;
import magic.parser.Parser;

public class TestAnalyse {

	@SuppressWarnings("unchecked")
	public <T> Expression<T> analyse(String t) {
		return (Expression<T>) Analyser.analyse(Parser.parse(t));
	}
	
	@Test 
	public void testLookup() {
		Context c=Context.createWith("foo",Constant.create(1));
		Expression<Integer> e=analyse("foo");
		assertEquals(Integer.valueOf(1),e.compute(c));
		
		try {
			e.compute(Context.EMPTY);
			fail("Should not be able to lookup value in empty context");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test 
	public void testConstant() {
		Expression<Integer> e=analyse("[1]");
		assertEquals(Tuple.of(1L),e.compute(Context.EMPTY));

	}
}
