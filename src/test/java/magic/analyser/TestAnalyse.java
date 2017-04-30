package magic.analyser;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.data.Symbol;
import magic.expression.Constant;
import magic.expression.Expression;
import magic.expression.Lookup;
import magic.lang.Context;

public class TestAnalyse {

	@Test 
	public void testLookup() {
		Context c=Context.createWith("foo",Constant.create(1));
		Expression<Integer> e=Lookup.create(Symbol.create("foo"));
		assertEquals(Integer.valueOf(1),e.compute(c));
		
		try {
			e.compute(Context.EMPTY);
			fail("Should not be able to lookup value in empty context");
		} catch (Throwable t) {
			// OK
		}
	}
}
