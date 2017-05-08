package magic.type;

import static org.junit.Assert.assertEquals;

import magic.Type;

import org.junit.Test;

import magic.Types;
import magic.ast.Node;
import magic.compiler.Analyser;
import magic.compiler.Reader;
import magic.data.Symbol;

public class TestTypes {
	private Class<?>  analyseClass(Object form) {
		Node<?> node=Analyser.analyse(form);
		Type type=node.getType();
		return type.getJavaClass();
	}
	
	@Test public void testConstantTypes() {
		assertEquals(Types.NULL,Analyser.analyse(null).getType());
		assertEquals(Double.class,analyseClass(1.0));
		assertEquals(String.class,analyseClass("foo"));
		assertEquals(Symbol.class,analyseClass(Reader.read("'foo")));
	}
}
