package magic.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSymbols {

	@Test public void testIntern() {
		Symbol s=Symbol.create("foo","bar");
		
		assertTrue(s.equals(Symbol.create("foo","bar")));
		assertTrue(s==Symbol.create("foo","bar"));
	}
	
}
