package magic.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import magic.Core;
import magic.Symbols;
import magic.ast.Constant;
import magic.ast.Lambda;
import magic.ast.Lookup;
import magic.ast.Node;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.UnresolvedException;

public class TestAnalyse {

	public <T> Node<T> compile(String t) {
		Node<?> node= Reader.read(t);
		return Compiler.compileNode(Core.INITIAL_CONTEXT,node);
	}
	
	@Test 
	public void testLookup() {
		Context c=Context.createWith("test/foo",Constant.create(1));
		Node<?> e=compile("test/foo");
		assertEquals(Integer.valueOf(1),e.compute(c));
		
		try {
			e.compute(Context.EMPTY);
			fail("Should not be able to lookup value in empty context");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testQuote() {
		EvalResult<?> r=Core.eval("(def sym 'x)");
		Context c2=r.getContext();
		assertEquals(Symbol.create("x"),c2.getValue("sym"));
	}
	
	@Test public void testZeroArgFn() {
		EvalResult<?> r=Core.eval("(defn f [] 1) (def x (f))");
		Context c2=r.getContext();
		assertEquals(Long.valueOf(1),c2.getValue("x"));
	}
	
	
	@Test public void testUnexpanded() {
		EvalResult<?> r=Core.eval("(def x a)");
		Context c2=r.getContext();
		try {
			c2.getValue("x");
			fail();
		} catch (UnresolvedException e) {
			assertEquals(Symbol.create("magic.core","a"),e.getSymbol());
		}
	}
	
	@Test public void testSyntaxQuote() {
		EvalResult<?> r=Core.eval("(def sym `x)");
		Context c2=r.getContext();
		assertEquals(Symbol.create("x"),c2.getValue("sym"));
	}
	
	@Test public void testUnquote() {
		EvalResult<?> r=Core.eval("(def a 1) (def v '[~a 2])");
		Context c2=r.getContext();
		Slot<?> s=c2.getSlot("v");
		assertNotNull(s);
		
		Object v=c2.getValue("v");
		assertNotNull(v);
		assertEquals(Tuple.of(1L,2L),v);
	}
	
	@Test 
	public void testInfiniteRecursiveLookup() {
		Context c=Context.createWith("foo",Lookup.create("foo"));
		Node<?> e=compile("foo");
		
		try {
			e.compute(c);
			fail("Should not be able to lookup with inifinite recursion");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test 
	public void testConstant() {
		Node<?> e=compile("1");
		assertEquals(Long.valueOf(1),e.compute(Context.EMPTY));
	}
	
	@Test
	public void testDeps() {
		Symbol foo=Symbol.create("user","foo");
		Symbol bar=Symbol.create("magic.core","bar");
		Symbol fn=Symbols.FN;
		assertEquals(Sets.of(foo),compile("user/foo").getDependencies());
		assertEquals(Sets.of(foo,bar),compile("[user/foo magic.core/bar]").getDependencies());
		assertEquals(Sets.of(fn),compile("(fn [bar] bar)").getDependencies());
		assertEquals(Sets.of(foo,fn),compile("(fn [bar] user/foo)").getDependencies());

		assertEquals(Sets.of(Symbols.QUOTE),compile("'(foo bar)").getDependencies());
	}
	
	@Test 
	public void testVector() {
		Context c=Context.createWith("test/foo",Constant.create(2L));
		Node<?> e=compile("[1 test/foo]");
		assertEquals(Tuple.of(1L,2L),e.compute(c));
	}
	
	@Test 
	public void testEmptyVector() {
		Node<?> e=compile("[]");
		assertEquals(Tuple.EMPTY,e.compute(Context.EMPTY));
	}
	
	@Test 
	public void testEmptyList() {
		Node<?> e=compile("()");
		assertEquals(PersistentList.EMPTY,e.compute(Context.EMPTY));
	}
	
	@Test 
	public void testLambda() {
		Node<?> e=compile("(fn [a] a)");
		assertEquals(Lambda.class,e.getClass());
		Context c=Core.INITIAL_CONTEXT.define("identity",e);
		Node<?> app=compile("(identity 2)");
		assertEquals(Long.valueOf(2),app.compute(c));

	}

}
