package magic.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import magic.Core;
import magic.Symbols;
import magic.data.Tuple;
import magic.lang.Context;

public class TestMacros {

//	@Test public void testExpander() {
//		Context c=RT.INITIAL_CONTEXT;
//		
//		EvalResult<?> r=Compiler.compile(c, 
//				"(def m (expander [_ [a]] 7 [1 2 3])) " +
//				"(def b (m 3))");
//		Context c2=r.getContext();
//		//System.out.println(c2.getExpression("f"));
//		Object res=c2.getValue("b");
//		//System.out.println("<END>");
//		assertEquals(Tuple.of(1L,2L,3L),res);
//	}
	
	
	@Test public void testBootStrapTypes() {
		Context c=Core.BOOTSTRAP_CONTEXT;
		assertTrue(c.getSlot(Symbols.DEFMACRO).isExpander());
	}
	
	
	@Test public void testMacro() {
		Context c=Core.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.eval(c, 
				"(defmacro m [_] 7 [1 2 3])");
		
		c=r.getContext();
		// System.out.println(c.getExpression("m"));
		r=Compiler.eval(c, 
				"(def b (m 3))");
		
		Context c2=r.getContext();
		//System.out.println(c2.getExpression("f"));
		Object res=c2.getValue("b");
		//System.out.println("<END>");
		assertEquals(Tuple.of(1L,2L,3L),res);
	}
}
