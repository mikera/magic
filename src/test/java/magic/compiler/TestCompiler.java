package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.lang.Context;

public class TestCompiler {

	@Test public void testCompileDef() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
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
		
		EvalResult<?> r=Compiler.compile(c, 
				"(do (def a 1) " +
				"    (def b a)) ");
		Context c2=r.getContext();
	
		assertEquals((Long)1L,c2.getValue("a"));
		assertEquals((Long)1L,c2.getValue("b"));
	}
	
	@Test public void testCompileLet() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				"(let [a 3] " +
				"    (def b a)) ");
		Context c2=r.getContext();
	
		assertEquals((Long)3L,c2.getValue("b"));
	}
	
	@Test public void testCompileLookup() {
		Context c=RT.INITIAL_CONTEXT;
		
		assertEquals((Long)1L,Compiler.compile(c, "(let [a 1] a)").getValue());
	}
	
	@Test public void testCompileVector() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				"(def a 1) " +
				"(def b 1) " +
				"(def v (let [a 3, c 5] " +
				"         [a b c]))");
		Context c2=r.getContext();
		// System.out.println(c2.getExpression("v"));
	
		assertEquals(Tuple.of(3L,1L,5L),c2.getValue("v"));
	}
	
	@Test public void testCompileLambda() {
		Context c=RT.INITIAL_CONTEXT;
		
		//System.out.println("<START>");
		EvalResult<?> r=Compiler.compile(c, 
				"(def a 1)" +
				"(def b 2)" +
				"(let[a 3, c 5] " +
				"   (def f (fn [c][a b c]))) "+
				"(def r (f 7))");
		Context c2=r.getContext();
		//System.out.println(c2.getExpression("f"));
		Object res=c2.getValue("r");
		//System.out.println("<END>");
		assertEquals(Tuple.of(3L,2L,7L),res);
	}
	
	@Test public void testCompileDefn() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				"(defn f [a] 2)" +
				"(def r (f 7))");
		Context c2=r.getContext();
		// System.out.println(c2.getExpression("f"));
		Object res=c2.getValue("r");
		assertEquals((Long)2L,res);
	}
	
	
	@Test public void testCompileVal() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				"(def a 1)" +
				"(def b 2)" +
				"a");
		Context c2=r.getContext();
		assertEquals((Long)1L,c2.getValue("a"));
		assertEquals((Long)1L,r.getValue());
	}
	
	@Test public void testConditional() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				"(if nil (def a 1) (def b 2))");
		Context c2=r.getContext();
		
		assertNull(c2.getSlot(Symbol.create("a")));
		assertEquals((Long)2L,c2.getValue("b"));
	}
	
	@Test public void testBaseUnquote() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				  "(def a 1)"
				+ "(def b ~a)"
				+ "(def a 2)"
				+ "(def c ~a)");
		Context c2=r.getContext();
		
		assertEquals((Long)1L,c2.getValue("b"));
		assertEquals((Long)2L,c2.getValue("c"));
	}
	
	@Test public void testDependencyUpdate() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				  "(defn g [c] (f c))"  
				+ "(def a 1)"
				+ "(defn f [c] a)"
				+ "(def a 2)"
				+ "(def b (g 3))");
		Context c2=r.getContext();
		
		assertEquals((Long)2L,c2.getValue("b"));
	}
	
	@Test public void testDependencies() {
		Context c=RT.INITIAL_CONTEXT;
		
		EvalResult<?> r=Compiler.compile(c, 
				  "(defn f [c] d)"
				+ "(def foo bar)"
				+ "(def a 1)"
				+ "(def b a)"
			    + "(defn f [c] b)");
		Context c2=r.getContext();
		
		assertEquals(Sets.of(),c2.getDependencies("a"));
		assertEquals(Sets.of(Symbol.create("b")),c2.getDependants("a"));
		assertEquals(Sets.of(Symbol.create("a")),c2.getDependencies("b"));
		assertEquals(Sets.of(Symbol.create("f")),c2.getDependants("b"));
		assertEquals(Sets.of(Symbol.create("b")),c2.getDependencies("f")); // TODO: should include fn??
		assertEquals(Sets.of(),c2.getDependants("f"));
	}
}
