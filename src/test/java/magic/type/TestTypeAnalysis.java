package magic.type;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import magic.Core;
import magic.Type;
import magic.Types;
import magic.ast.Constant;
import magic.ast.Node;
import magic.compiler.Compiler;
import magic.compiler.Reader;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Maps;
import magic.data.Symbol;

public class TestTypeAnalysis {
	private <T> Node<T> compile(String s) {
		return Compiler.expand(Core.INITIAL_CONTEXT, Reader.read(s));
	}
	
	private Class<?>  analyseClass(Node<?> form) {
		Node<?> node=Compiler.expand(Core.BOOTSTRAP_CONTEXT, form);
		Type type=node.getType();
		return type.getJavaClass();
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testConstantTypes() {
		assertEquals(Types.NULL,Node.toNode(null).getType());
		assertEquals(Double.class,analyseClass(Constant.create(1.0)));
		assertEquals(String.class,analyseClass(Constant.create("foo")));
		assertEquals(Symbol.class,compile("'foo").eval(Core.INITIAL_CONTEXT,(APersistentMap<Symbol, Object>) Maps.EMPTY).getValue().getClass());
	}
	
	@Test public void testDataTypes() {
		assertEquals(APersistentVector.class,analyseClass(Reader.read("[1 a]")));
	}
	
	@Test public void testDoTypes() {
		Node<?> emptyDo=compile("(do)");
		assertEquals(Types.NULL,emptyDo.getType());
		
		assertEquals(String.class,analyseClass(Reader.read("(do [1 a] 2 \"foo\")")));
	}
	
	@Test public void testLetTypes() {
		Node<?> emptyLet=compile("(let [a 3])");
		//System.out.println(emptyLet);
		assertEquals(Types.NULL,emptyLet.getType());
		
		assertEquals(Long.class,analyseClass(Reader.read("(let [a 1] 2)")));
	}
	
	@Test public void testLambdaTypes() {
		Node<?> fnExpr=compile("(fn [a b] 3)");
		//System.out.println(fnExpr);
		Type type=fnExpr.getType();
		FunctionType fType=(FunctionType)type; 
		assertEquals(Long.class,fType.getReturnType().getJavaClass());
	
	}
}
