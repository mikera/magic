package magic.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import magic.Core;
import magic.ast.ListForm;
import magic.ast.Node;
import magic.ast.Vector;
import magic.data.IPersistentVector;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.lang.Context;
import magic.lang.Keywords;
import magic.lang.Symbols;

public class TestParser {

	private static final Context INITIAL=Core.INITIAL_CONTEXT;
	
	@Test public void testString() {
		Node<?> c=Reader.read("\"foo\"");
		assertEquals("foo",c.getValue());
	}
	
	@Test public void testDouble() {
		assertEquals(Double.valueOf(-3.0),Reader.read("-3.0").getValue());
		assertEquals(Double.valueOf(.3e10),Reader.read(".3e10").getValue());
		assertEquals(Double.valueOf(-.3e10),Reader.read("-0.3e10").getValue());
	}
	
	@Test public void testNil() {
		assertNull(Reader.read("nil").getValue());
	}
	
	@Test public void testKeywords() {
		assertEquals(Keyword.class,Reader.read(":foo").getValue().getClass());
	}
	
	@Test public void testBooleans() {
		assertTrue((Boolean)Reader.read("true").getValue());
		assertFalse((Boolean)Reader.read("false").getValue());
	}

	
	@Test public void testLong() {
		assertEquals(Long.valueOf(-3),Reader.read("-3").getValue());
		assertEquals(Long.valueOf(3000),Reader.read("+3000").getValue());
		assertEquals(Long.valueOf(-21),Reader.read("-21").getValue());
	}
	
	@Test public void testChars() {
		assertEquals(' ',Reader.read("\\space").getValue());
		assertEquals('\t',Reader.read("\\tab").getValue());
		assertEquals('\f',Reader.read("\\formfeed").getValue());
		assertEquals('\b',Reader.read("\\backspace").getValue());
		assertEquals('\n',Reader.read("\\newline").getValue());
		assertEquals('\r',Reader.read("\\return").getValue());
		assertEquals('*',Reader.read("\\u002A").getValue());
		assertEquals('\u03A9',Reader.read("\\u03a9").getValue());
		assertEquals('\uAABB',Reader.read("\\uaAbB").getValue());
	}
	
	@Test public void testReadSymbol() {
		assertEquals(Symbol.create("foo"),Reader.readSymbol(" foo"));
		assertEquals(Symbol.create("+"),Reader.readSymbol("+"));
		assertEquals(Symbol.create("a.b"),Reader.readSymbol("a.b"));
		assertEquals(Symbol.create("foo","bar"),Reader.readSymbol("foo/bar"));
		assertEquals(Symbol.create("foo","bar"),Reader.readSymbol(" foo/bar "));
		assertEquals(Symbols.UNDERSCORE,Reader.readSymbol("_"));
	}
	
	@Test public void testQualifiedSymbol() {
		Symbol sym= Reader.read("foo/bar").getSymbol();
		assertEquals(Symbol.create("foo","bar"),sym);
		assertEquals("foo",Reader.readSymbol("foo//").getNamespace());
		assertEquals(Symbol.create("foo","/"),Reader.read("foo//").getSymbol());
	}
	
	@Test public void testUnqualifiedSymbol() {
		assertEquals(Symbol.create("foo"),Reader.read("foo").getSymbol());
		assertEquals(Symbol.create("/"),Reader.read("/").getSymbol());
	}
	
	@Test public void testMetaSymbol() {
		Node<?> n=Reader.read("^:bar foo");
		assertEquals(Symbol.create("foo"),n.getSymbol());
		Node<?> metaNode=(Node<?>) n.meta().get(Keywords.META);
		assertEquals(Keyword.create("bar"),metaNode.getValue());
	}
	
	@Test public void testEmptyVector() {
		Node<?> ev=Reader.read("[]");
		assertEquals(Vector.class,ev.getClass());
		assertEquals(0,((Vector<?>)ev).size());
	}
	
	@Test public void testeIgnoreForm() {
		Node<?> ev=Reader.read("[1 #_2 3]");
		assertEquals(Tuple.of(1L,3L),ev.toForm());
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testVector() {
		Reader.read("[1]");
		Node<?> c=Reader.read("[-3 -1.0 \"foo\"]");
		IPersistentVector<Object> exps=(IPersistentVector<Object>) c.toForm();
		assertEquals(3,exps.size());
		assertEquals(Long.valueOf(-3),exps.get(0));
		assertEquals(Double.valueOf(-1.0),exps.get(1));
		assertEquals("foo",exps.get(2));
	}
	
	@Test public void testList() {
		Object c=Reader.read("(-3 -1.0 \"foo\")");
		ListForm exps=(ListForm) c;
		assertEquals(3,exps.size());
		assertEquals(Long.valueOf(-3),exps.get(0).getValue());
		assertEquals(Double.valueOf(-1.0),exps.get(1).getValue());
		assertEquals("foo",exps.get(2).getValue());
	}
	
	@Test public void testSet() {
		Object s1= Compiler.compile(INITIAL,"#{3}").getValue();
		assertEquals(Sets.of(3L),s1);
		Object s2= Compiler.compile(INITIAL,"#{1,3,2,3,1}").getValue();
		assertEquals(Sets.of(1L,2L,3L),s2);
	}
	
	@Test public void testSetDuplicates() {
		Object s2= Compiler.compile(INITIAL,"#{:foo :foo}").getValue();
		assertEquals(Sets.of(Keyword.create("foo")),s2);
	}
	
	@Test public void testSetDuplicates2() {
		Object s2= Compiler.compile(INITIAL,"#{'foo 'foo}").getValue();
		assertEquals(Sets.of(Symbol.create("foo")),s2);
	}
	
	@Test public void testMap() {
		Object c= Compiler.compile(INITIAL,"{1 2}").getValue();
		assertEquals(Maps.create(1L, 2L),c);
	}
	
	@Test public void testQuote() {
		assertEquals(Reader.read("(quote foo/bar)").toForm(),Reader.read("'foo/bar").toForm());
		assertEquals(Reader.read("(unquote (quote foo))").toForm(),Reader.read("~'foo").toForm());
		assertEquals(Reader.read("(unquote (syntax-quote foo))").toForm(),Reader.read("~`foo").toForm());
	}
	
	@Test public void testExtraInputFail() {
		try {
		  Reader.read("{1 2 3} [1]");
		  fail("Shouldn't be able to parse multiple expressions!");
		} catch (Throwable t) {
			// OK
		}
	}
	
	@Test public void testMapFail() {
		try {
		  Reader.read("{1 2 3}");
		  fail("Shouldn't be able to create this map!");
		} catch (Throwable t) {
			//System.out.println(t.getMessage());
			// OK
		}
	}
}
