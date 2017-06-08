package magic.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import magic.RT;
import magic.ast.Node;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.fn.AFn;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.UnresolvedException;

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
		//System.out.println(c2.getNode("f"));
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
	
	@Test public void testUnquote() {
		Context c=RT.INITIAL_CONTEXT;
		// TODO: make work with quoted form?
		EvalResult<?> r=Compiler.compile(c, 
				  "(def a 2)"
				+ "(def b [1 ~a])");
		Context c2=r.getContext();
		
		assertEquals(Tuple.of(1L,2L),c2.getValue("b"));
	}
	
// TODO: think about dependencies on special forms
//	@Test public void testOverwriteSpecialForm() {
//		Context c=RT.INITIAL_CONTEXT;
//		
//		EvalResult<?> r=Compiler.compile(c, 
//				  "(def a (do 2))"  
//				+ "(def do (fn [a] 3))"
//				+ "(def b a)");
//		Context c2=r.getContext();
//		
//		assertEquals((Long)3L,c2.getValue("b"));
//	}
	
	@SuppressWarnings("unused")
	@Test public void testDependencyUpdate() {
		Context c=RT.INITIAL_CONTEXT;
		
		Context c1=Compiler.compile(c, 
				  "(defn g [c] (f c))"  
				+ "(def a 1)").getContext();
		
		Slot<?> ogSlot=c1.getSlot("g");
		assertFalse(ogSlot.isComputed());
		Object og;
		try {
			og=c1.getValue("g");
			fail("Should not be able to compute g at this point!"); // TODO: what happens here?
		} catch (UnresolvedException e) {
			assertEquals(e.getSymbol(),Symbol.create("f"));
		}
		assertFalse(ogSlot.isComputed());
		
		{ // check dependency exists
			Node<?> g=c1.getNode("g");
			assertTrue(g.getDependencies().contains(Symbol.create("f")));
			assertTrue(c1.getDependencies(Symbol.create("g")).contains(Symbol.create("f")));
			assertTrue(c1.getDependants(Symbol.create("f")).contains(Symbol.create("g")));
		}

		EvalResult<?> r=Compiler.compile(c1, 
				  "(defn f [c] a)"
				+ "(def a 2)"
				+ "(def b (g 3))");
		Context c2=r.getContext();
		
		// check correct dependencies exists
		Slot<?> aSlot=c2.getSlot("a");
		Slot<?> bSlot=c2.getSlot("b");
		Slot<?> fSlot=c2.getSlot("f");
		Slot<?> gSlot=c2.getSlot("g");
		
		Node<?> g=c2.getNode("g");
		assertTrue(g.getDependencies().contains(Symbol.create("f")));
		Node<?> f=c2.getNode("f");
		assertTrue(f.getDependencies().contains(Symbol.create("a")));
		assertTrue(c2.getDependants("a").contains(Symbol.create("f")));
		assertTrue(c2.calcDependants(Symbol.create("a")).contains(Symbol.create("b")));
		assertEquals(fSlot.getDependencies(),f.getDependencies());
		
		// compute b, should transitively compute g and f
		assertFalse(aSlot.isComputed());
		assertFalse(bSlot.isComputed());
		assertFalse(fSlot.isComputed());
		assertFalse(gSlot.isComputed());
		Object bVal=c2.getValue("b");
		assertTrue(bSlot.isComputed());
		assertTrue(gSlot.isComputed());
		assertTrue(fSlot.isComputed());
		assertTrue(aSlot.isComputed());
		
		Object fVal=fSlot.getValue();
		assertNotNull(fVal);
		assertEquals((Long)2L,((AFn<?>)fVal).applyToArray(4L));
		
		
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
		Node<?> f=c2.getNode("f");
		assertEquals(Sets.of(Symbol.create("b")),f.getDependencies());
		
		assertEquals(Sets.of(),c2.getDependencies("a"));
		assertEquals(Sets.of(Symbol.create("b")),c2.getDependants("a"));
		assertEquals(Sets.of(Symbol.create("a")),c2.getDependencies("b"));
		assertEquals(Sets.of(Symbol.create("f")),c2.getDependants("b"));
		assertEquals(Sets.of(Symbol.create("b")),c2.getDependencies("f")); // TODO: should include fn??
		assertEquals(Sets.of(),c2.getDependants("f"));
	}
}
