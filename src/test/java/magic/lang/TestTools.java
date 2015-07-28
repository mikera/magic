package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTools {

	@Test public void testHashCode() {
		assertEquals(0, Tools.hash(null));
	}
}
