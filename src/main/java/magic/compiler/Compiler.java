package magic.compiler;

import magic.ast.List;
import magic.ast.Node;
import magic.data.APersistentMap;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;

public class Compiler {

	@SuppressWarnings("unchecked")
	public static <T> Node<T> compileNode(Context context, Node<? super T> node) {
		node=Analyser.expand(context, node);
		node=(Node<? super T>) node.optimise();
		return (Node<T>)node;
	}
	
	/**
	 * Compiles and evaluates an AST node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<? super T> node) {
		node=compileNode(context,node);
		EvalResult<T> result;
		try {
			result=(EvalResult<T>) node.eval(context,(APersistentMap<Symbol, Object>) Maps.EMPTY);
		} catch (Throwable t) {
			throw new Error("Error evaluating node:" + node,t);
		}
		return result;
	}

	/**
	 * Compiles and evaluates code in the given context.
	 * @param c
	 * @param string
	 * @return
	 */
	public static EvalResult<?> compile(Context c, String string) {
		List forms=Reader.readAll(string);
		int n=forms.size();
		
		EvalResult<?> r=new EvalResult<>(c,null);
		for (int i=0; i<n; i++) {
			Node<?> form=forms.get(i);
			r=compile(r.getContext(),form);
		}
		return r;
	}

}

