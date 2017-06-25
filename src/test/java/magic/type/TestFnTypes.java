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
		EvalResult<?> er=Compiler.compile(Core.INITIAL_CONTEXT, string);
		return er.getContext();
	}
	
	@Test public void testIdentityApply() {
		Context c=compileWith("(defn identity [a] a)");
		
		assertTrue(analyseType(c,"(identity 2)").contains(Types.LONG));
	}



}
