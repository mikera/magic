package magic.core;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.Core;
import magic.data.Tuple;

public class TestEdgeCases {
	@SuppressWarnings("unchecked")
	private <T> T exec(String code) {
		return (T) Core.eval(code).getValue();
	}
	
	@Test public void testDefUnquote() {
		// unquote should create a symbol that can be used with def
		assertEquals((Long)2L,exec("(def ~'a 2) a"));
		assertEquals((Long)2L,exec("(def ~(symbol \"a\") 2) a"));
	}
	
	@Test public void testArgUnquote() {
		// unquote should create a symbol that can be used in parameter vector
		assertEquals((Long)3L,exec(
				"(defn foo [~'a] (+ 2 a)) "
				+ "(foo 1)"));
	}
	
	@Test public void testContextTravel() {
		assertEquals(Tuple.of(1L),exec(
				  "(def a 1) "
				+ "(def ctx *context*)"
				+ "(def a 2)"
				+ "(context ctx)"
				+ "[a]"));
	}
}
