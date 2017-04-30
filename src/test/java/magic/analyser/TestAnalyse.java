package magic.analyser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import magic.compiler.Analyser;
import magic.compiler.Parser;
import magic.data.PersistentList;
import magic.data.Tuple;
import magic.expression.Constant;
import magic.expression.Expression;
import magic.expression.Lookup;
import magic.lang.Context;

public class TestAnalyse {

	@SuppressWarnings("unchecked")
	public <T> Expression<T> analyse(String t) {
		return (Expression<T>) Analyser.analyse(Parser.parse(t));
	}
	
	@Test 
	public void testLookup() {
		Context c=Context.createWith("foo",Constant.create(1));
		Expression<?> e=analyse("foo");
		assertEquals(Integer.valueOf(1),e.compute(c));
		
		try {
			e.compute(Context.EMPTY);
			fail("Should not be able to lookup value in empty context");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test 
	public void testInfiniteRecursiveLookup() {
		Context c=Context.createWith("foo",Lookup.create("foo"));
		Expression<?> e=analyse("foo");
		
		try {
			e.compute(c);
			fail("Should not be able to lookup with inifinite recursion");
		} catch (StackOverflowError t) {
			// OK
		}
	}
	
	@Test 
	public void testConstant() {
		Expression<?> e=analyse("1");
		assertEquals(Long.valueOf(1),e.compute(Context.EMPTY));

	}
	
	@Test 
	public void testVector() {
		Context c=Context.createWith("foo",Constant.create(2L));
		Expression<?> e=analyse("[1 foo]");
		assertEquals(Tuple.of(1L,2L),e.compute(c));
	}
	
	@Test 
	public void testEmptyVector() {
		Expression<?> e=analyse("[]");
		assertEquals(Tuple.EMPTY,e.compute(Context.EMPTY));
	}
	
	@Test 
	public void testEmptyList() {
		Expression<?> e=analyse("()");
		assertEquals(PersistentList.EMPTY,e.compute(Context.EMPTY));
	}

}
