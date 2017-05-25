package magic.compiler;

import magic.ast.List;
import magic.ast.Node;
import magic.ast.Vector;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;

public class Compiler {

	/*
	 * Compiles and evaluates an AST node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<T> node) {
		node=Analyser.expand(context, node);
		node=Analyser.analyse(context, node);
		return node.eval(context,(APersistentMap<Symbol, Object>) Maps.EMPTY);
	}

	public static EvalResult<?> compile(Context c, String string) {
		List<?> forms=Reader.readAll(string);
		int n=forms.size();
		
		EvalResult<?> r=new EvalResult<>(c,null);
		for (int i=0; i<n; i++) {
			r=compile(r.getContext(),forms.get(i));
		}
		return r;
	}

}

