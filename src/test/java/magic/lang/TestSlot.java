package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.expression.Constant;

public class TestSlot {

	@Test public void testConstantSlot() {
		Slot s=new Slot(new Constant(1));
		
		Context c=new Context();
		
		assertEquals(1,s.getValue(c));
	}
}
