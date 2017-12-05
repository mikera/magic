package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.Core;
import magic.ast.Node;
import magic.lang.Context;

public class TestOptimise {
	
	/**
	 * Compiles and optimises a Node
	 * @param s
	 * @return
	 */
	private Node<?> compile(String s) {
		Context context=Core.INITIAL_CONTEXT;
		Node<?> raw=Reader.read(s);
		Node<?> compiled=Compiler.compileNode(context, raw);
		return compiled;
	}
	
	@Test public void TestConstants() {
		assertTrue(compile("()").isConstant());
		assertTrue(compile("[]").isConstant());
		assertTrue(compile("1").isConstant());
		assertTrue(compile("[1 2 3]").isConstant());
		assertTrue(compile("(list 1 2)").isConstant());
		assertTrue(compile(":foo").isConstant());
		// assertTrue(compile("'foo").isConstant()); // TODO sanity check this
	}
	
	@Test public void TestNonConstants() {
		assertFalse(compile("a").isConstant());
		assertFalse(compile("(f a)").isConstant());
		assertFalse(compile("[1 a 3]").isConstant());
	}

	
	@Test public void TestConstantFolding() {
		assertEquals(1L,compile("(if true 1 2)").getValue());
		assertEquals(2L,compile("(if nil 1 2)").getValue());
	}
}
