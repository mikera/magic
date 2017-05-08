package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.ast.Constant;
import magic.ast.Node;
import magic.ast.Lambda;
import magic.ast.Lookup;
import magic.compiler.Analyser;
import magic.compiler.Reader;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.lang.Context;
import magic.lang.Symbols;

public class TestAnalyse {

	@SuppressWarnings("unchecked")
	public <T> Node<T> analyse(String t) {
		return (Node<T>) Analyser.analyse(Reader.read(t));
	}
	
	@Test 
	public void testLookup() {
		Context c=Context.createWith("foo",Constant.create(1));
		Node<?> e=analyse("foo");
		assertEquals(Integer.valueOf(1),e.compute(c));
		
		try {
			e.compute(Context.EMPTY);
			fail("Should not be able to lookup value in empty context");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testQuote() {
		Context c=RT.INITIAL_CONTEXT;
		EvalResult<?> r=Compiler.compile(c,"(def sym 'x)");
		Context c2=r.getContext();
		assertEquals(Symbol.create("x"),c2.getValue("sym"));
	}
	
	@Test public void testUnquote() {
		Context c=RT.INITIAL_CONTEXT;
		EvalResult<?> r=Compiler.compile(c,"(def a 1) (def v '[~a 2])");
		Context c2=r.getContext();
		// TODO: fix this!
		Object v=c2.getValue("v");
		assertNotNull(v);
		// assertEquals(Tuple.of(1L,2L),v);
	}
	
	@Test 
	public void testInfiniteRecursiveLookup() {
		Context c=Context.createWith("foo",Lookup.create("foo"));
		Node<?> e=analyse("foo");
		
		try {
			e.compute(c);
			fail("Should not be able to lookup with inifinite recursion");
		} catch (StackOverflowError t) {
			// OK
		}
	}
	
	@Test 
	public void testConstant() {
		Node<?> e=analyse("1");
		assertEquals(Long.valueOf(1),e.compute(Context.EMPTY));
	}
	
	@Test
	public void testDeps() {
		Symbol foo=Symbol.create("foo");
		Symbol bar=Symbol.create("bar");
		assertEquals(Sets.of(foo),Analyser.analyse(Reader.read("foo")).getDependencies());
		assertEquals(Sets.of(foo,bar),Analyser.analyse(Reader.read("[foo bar]")).getDependencies());
		assertEquals(Sets.of(),Analyser.analyse(Reader.read("(fn [bar] bar)")).getDependencies());
		assertEquals(Sets.of(foo),Analyser.analyse(Reader.read("(fn [bar] foo)")).getDependencies());

		assertEquals(Sets.of(Symbols.QUOTE),Analyser.analyse(Reader.read("'(foo bar)")).getDependencies());
	}
	
	@Test 
	public void testVector() {
		Context c=Context.createWith("foo",Constant.create(2L));
		Node<?> e=analyse("[1 foo]");
		assertEquals(Tuple.of(1L,2L),e.compute(c));
	}
	
	@Test 
	public void testEmptyVector() {
		Node<?> e=analyse("[]");
		assertEquals(Tuple.EMPTY,e.compute(Context.EMPTY));
	}
	
	@Test 
	public void testEmptyList() {
		Node<?> e=analyse("()");
		assertEquals(PersistentList.EMPTY,e.compute(Context.EMPTY));
	}
	
	@Test 
	public void testLambda() {
		Node<?> e=analyse("(fn [a] a)");
		assertEquals(Lambda.class,e.getClass());
		Context c=Context.createWith("identity",e);
		Node<?> app=Analyser.analyse(c,Reader.read("(identity 2)"));
		assertEquals(Long.valueOf(2),app.compute(c));

	}

}
