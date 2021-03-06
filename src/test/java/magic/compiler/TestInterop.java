package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.Core;
import magic.RT;
import magic.lang.Context;

public class TestInterop {
	Context INITIAL=Core.INITIAL_CONTEXT;

	@Test public void testInstanceCall() {
		Context c=INITIAL;
		
		EvalResult<?> r=Compiler.eval(c, 
				"(def len (. \"foo\" (length)))");
		Context c2=r.getContext();
		//System.out.println(c2.getExpression("f"));
		Object res=c2.getValue("len");
		//System.out.println("<END>");
		assertEquals((Integer)3,res);
	}
	
	@Test public void testInstanceCall2() {
		Context c=INITIAL;
		
		EvalResult<?> r=Compiler.eval(c, 
				"(def len (. \"foo\" length))");
		Context c2=r.getContext();
		Object res=c2.getValue("len");
		assertEquals((Integer)3,res);
	}
	
	@Test public void testStaticCall() {
		@SuppressWarnings("unused")
		Context c=INITIAL;
		
		try {
			assertNotNull(RT.class.getMethod("toString", Object.class));
		} catch (Throwable t) {
			fail("Exception getting method");
		}
		
// TODO: fix identification of method via parameter types
//		EvalResult<?> r=Compiler.compile(c, 
//				"(def s (. magic.RT toString 1))");
//		Context c2=r.getContext();
//		Object res=c2.getValue("s");
//		assertEquals("1",res);
	}
	
	@Test public void testInteropForms() {
		assertEquals((Integer)3,Core.eval("(. \"foo\" length)").getValue());
		assertEquals((Integer)3,Core.eval("(.length \"foo\")").getValue());
	}
	
}
