package magic.compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.RT;
import magic.ast.Node;
import magic.lang.Context;

public class TestOptimise {
	
	/**
	 * Compiles and optimises a Node
	 * @param s
	 * @return
	 */
	private Node<?> compile(String s) {
		Context context=RT.INITIAL_CONTEXT;
		Node<?> raw=Reader.read(s);
		Node<?> expanded=Compiler.expand(context, raw);
		Node<?> compiled=Compiler.compileNode(context, expanded);
		return compiled;
	}
	
	@Test public void TestConstants() {
		assertTrue(compile("1").isConstant());
		assertTrue(compile("[1 2 3]").isConstant());
	}
}
