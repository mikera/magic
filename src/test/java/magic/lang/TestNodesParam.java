package magic.lang;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import magic.RT;
import magic.Type;
import magic.ast.*;
import magic.compiler.Analyser;
import magic.compiler.Compiler;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.fn.IFn0;

@RunWith(Parameterized.class)
public class TestNodesParam {
	@Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data=new ArrayList<>();
        for (Node<?> t:exampleNodes) {
        	data.add(new Object[] {t});
        }
        return data;
    }
    
	@SuppressWarnings("unchecked")
	static Node<?>[] exampleNodes=new Node<?>[] {
		Constant.create(1),
		Constant.create(null),
		Vector.create(Constant.create(1)),
		DeferredConstant.create(new IFn0<String>() {
			@Override
			public String apply() {
				return "Hello";
			}}),
		Do.create(Constant.create("foo")),
		Let.create(new Symbol[]{Symbol.create("a")},new Constant[]{Constant.create(3.0)},Lookup.create("a")),
		Lambda.create(Vectors.of(Symbol.create("a")), Constant.create(Keyword.create("foo"))),
		If.createIf(Constant.create(true), Constant.create(1), Constant.create("foo")),
		InvokeReflective.create(Constant.create("bar"), Symbol.create("length"), new Node[0])
		
	};
	
	private Node<?> node;
	
    public TestNodesParam(Object node) {
        this.node=(Node<?>) node;
    }
	
	@Test 
	public void testEval() {
		Context c=Context.EMPTY;
		APersistentSet<Symbol> deps = node.getDependencies();
		Type type=node.getType();
		Object v=node.compute(c);
		assertNotNull(deps);
		assertNull(node.getSourceInfo());
		// System.out.println(node + " = " +RT.toString(v) +" : "+type);
		assertTrue(type.checkInstance(v));
	}
	
	@Test 
	public void testRoundTrip() {
		Context c=RT.BOOTSTRAP_CONTEXT;
		Node<?> node1=Compiler.expand(c,Analyser.analyse(node.toForm())).optimise();
		Object form1=node1.toForm();
		Node<?> node2=Compiler.expand(c,Analyser.analyse(form1)).optimise();
		
		assertEquals(form1,node2.toForm());
		assertEquals(node1.getClass(),node2.getClass());
		assertEquals(RT.toString(node1),RT.toString(node2));
	}
}
