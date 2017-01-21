package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.expression.Constant;

public class TestSlot {

	@Test public void testConstantSlot() {
		Slot<Integer> s=new Slot<Integer>(Constant.create(1));
		
		Context c=new Context();
		
		assertEquals(1,s.getValue(c));
	}
}
