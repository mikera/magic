package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.lang.Context;

public class TestCompiler {

	@Test public void testCompileDef() {
		Context c=RT.INITIAL_CONTEXT;
		
		Result<?> r=Compiler.compile(c, 
				"(def a 1) " +
				"(def b 2) " +
			    "(def id (fn [a] a)) "+
			    "(def fst (fn [a b] a)) "+
				"(def c (id a)) "+
				"(def d (fst 7 8))");
		Context c2=r.getContext();
		assertEquals((Long)1L,c2.getValue("a"));
		assertEquals((Long)2L,c2.getValue("b"));
		assertEquals((Long)1L,c2.getValue("c"));
		assertEquals((Long)7L,c2.getValue("d"));
	}
	
	@Test public void testCompileVal() {
		Context c=RT.INITIAL_CONTEXT;
		
		Result<?> r=Compiler.compile(c, 
				"(def a 1) " +
				"(def b 2) " +
				"a");
		Context c2=r.getContext();
		assertEquals((Long)1L,c2.getValue("a"));
		assertEquals((Long)1L,r.getValue());
	}
}
