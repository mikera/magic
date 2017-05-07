package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.data.Symbol;
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
	
	@Test public void testCompileDo() {
		Context c=RT.INITIAL_CONTEXT;
		
		Result<?> r=Compiler.compile(c, 
				"(do (def a 1) " +
				"    (def b a)) ");
		Context c2=r.getContext();
	
		assertEquals((Long)1L,c2.getValue("a"));
		assertEquals((Long)1L,c2.getValue("b"));
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
	
	@Test public void testConditional() {
		Context c=RT.INITIAL_CONTEXT;
		
		Result<?> r=Compiler.compile(c, 
				"(if nil (def a 1) (def b 2))");
		Context c2=r.getContext();
		
		assertNull(c2.getSlot(Symbol.create("a")));
		assertEquals((Long)2L,c2.getValue("b"));
	}
}
