package magic.type;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.Core;
import magic.Type;
import magic.Types;
import magic.ast.Node;
import magic.compiler.Compiler;
import magic.compiler.EvalResult;
import magic.compiler.Reader;
import magic.lang.Context;

public class TestFnTypes {
	
	private Type  analyseType(Context c,String  form) {
		Node<?> node=Compiler.expand(c, Reader.read(form));
		Type type=node.getType();
		return type;
	}
	
	private Context compileWith(String string) {
		EvalResult<?> er=Compiler.eval(Core.INITIAL_CONTEXT, string);
		return er.getContext();
	}
	
	@Test public void testIdentityApply() {
		Context c=compileWith("(defn identity [a] a)");
		
		assertTrue(analyseType(c,"(identity 2)").contains(Types.LONG));
	}

	@Test public void testVariadicTypes() {
		FunctionType f1=FunctionType.createMultiArity(Types.ANY, Types.LONG);
		FunctionType f2=FunctionType.createMultiArity(Types.LONG, Types.ANY);
		FunctionType f3=FunctionType.create(Types.LONG); // zero arity
		
		assertFalse(f3.isVariadic());
		assertTrue(f1.contains(f2));
		assertTrue(f1.contains(f3));
		assertTrue(f2.contains(f3));

		assertFalse(f3.contains(f1));
		assertFalse(f2.contains(f1));
		assertFalse(f3.contains(f2)); // doesn't contain multi-arity case

	}

}
