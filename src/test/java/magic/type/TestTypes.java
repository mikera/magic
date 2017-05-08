package magic.type;

import static org.junit.Assert.assertEquals;

import magic.Type;

import org.junit.Test;

import magic.Types;
import magic.ast.Node;
import magic.compiler.Analyser;
import magic.compiler.Reader;
import magic.data.APersistentVector;
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
	
	@Test public void testDataTypes() {
		assertEquals(APersistentVector.class,analyseClass(Reader.read("[1 a]")));
	}
	
	@Test public void testDoTypes() {
		Node<?> emptyDo=Analyser.analyse(Reader.read("(do)"));
		System.out.println(emptyDo);
		assertEquals(Types.NULL,emptyDo.getType());
		
		assertEquals(String.class,analyseClass(Reader.read("(do [1 a] 2 \"foo\")")));
	}
	
	@Test public void testLetTypes() {
		Node<?> emptyLet=Analyser.analyse(Reader.read("(let [a 3])"));
		System.out.println(emptyLet);
		assertEquals(Types.NULL,emptyLet.getType());
		
		assertEquals(Long.class,analyseClass(Reader.read("(let [a 1] 2)")));
	}
}
