package magic.compiler;

import magic.ast.List;
import magic.ast.Node;
import magic.data.APersistentMap;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;

public class Compiler {
	
	/**
	 * Expands a form in a given context
	 * @param context
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Node<T> expand(Context context, Node<?> form) {
		AExpander ex=Expanders.INITAL_EXPANDER;
		return (Node<T>) ex.expand(context, form, ex);
	}

	@SuppressWarnings("unchecked")
	public static <T> Node<T> compileNode(Context context, Node<? super T> node) {
		node=Compiler.expand(context, node);
		node=(Node<? super T>) node.optimise();
		return (Node<T>)node;
	}
	
	/**
	 * Compiles and evaluates a node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<? super T> node) {
		return compile(context,node,(APersistentMap<Symbol, Object>) Maps.EMPTY);
		
	}
	
	/**
	 * Compiles and evaluates a node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<? super T> node, APersistentMap<Symbol, Object> bindings) {
		node=compileNode(context,node);
		EvalResult<T> result;
		try {
			result=(EvalResult<T>) node.eval(context,bindings);
		} catch (Throwable t) {
			throw new Error("Error evaluating node: " + node +" : "+t.getMessage(),t);
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
		
		EvalResult<?> r=null;
		for (int i=0; i<n; i++) {
			Node<?> form=forms.get(i);
			r=compile(c,form);
			c=r.getContext();
		}
		return r;
	}


}

