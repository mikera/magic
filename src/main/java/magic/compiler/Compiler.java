package magic.compiler;

import magic.ast.ListForm;
import magic.ast.Node;
import magic.data.APersistentMap;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;

public class Compiler {
	
	/**
	 * Expands a form in a given context.
	 * 
	 * Expansion includes:
	 * - Running the default expander on the form
	 * - Accumulating dependencies from successive expansions
	 * 
	 * @param context
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Node<T> expand(Context context, Node<?> form) {
		AExpander ex=Expanders.INITAL_EXPANDER;
		return (Node<T>) ex.expand(context, form, ex);
	}
	
	/**
	 * Analyses an expanded node in the given context. Performs type checking etc.
	 * @param context
	 * @param node
	 * @return
	 */
	private static Node<?> analyse(AnalysisContext context, Node<?> node) {
		try {
			return node.analyse(context);
		} catch (Throwable t) {
			throw new Error(t.getMessage()+"\n"+
		              "while analysing: " + node,t); 
		}
	}

	/**
	 * Expands, compiles and optimises a node in the given context
	 * @param context
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Node<T> compileNode(Context context, Node<?> node) {
		node=Compiler.expand(context, node);
		node=Compiler.analyse(AnalysisContext.create(context),node);
		node=node.optimise();
		// TODO: should specialise to context here?
		return (Node<T>)node;
	}
	
	/**
	 * Expands, compiles and optimises a node in the given context with specified local bindings
	 * @param context
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Node<T> compileNode(Context context, APersistentMap<Symbol, Object> bindings, Node<?> node) {
		node=Compiler.expand(context, node);
		node=Compiler.analyse(AnalysisContext.create(context,bindings),node);
		node=node.optimise();
		// TODO: should specialise to context here?
		return (Node<T>)node;
	}


	/**
	 * Compiles and evaluates a node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> eval(Context context, Node<?> node) {
		APersistentMap<Symbol, Object> bindings=Maps.empty();
		return (EvalResult<T>) eval(context,node, bindings);
		
	}
	 
	/**
	 * Compiles and evaluates a node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> eval(Context context, Node<?> node, APersistentMap<Symbol, Object> bindings) {
		EvalResult<T> result;
		Node<? super T> compiledNode;
		compiledNode=compileNode(context,node);
		try {
			result=(EvalResult<T>) compiledNode.eval(context,bindings);
		} catch (Throwable t) {
			throw new magic.Error(t.getMessage()+"\n"+
		              "while evaluating code: " + node,t); 
		}

		return result;
	}

	/**
	 * Compiles and evaluates code in the given context.
	 * @param c
	 * @param string
	 * @return
	 */
	public static EvalResult<?> eval(Context c, String string) {
		ListForm forms=Reader.readAll(string);
		int n=forms.size();
		if (n==0) return EvalResult.create(c, null);
		
		EvalResult<?> r=null;
		for (int i=0; i<n; i++) {
			Node<?> form=forms.get(i);
			r=eval(c,form,Maps.empty());
			c=r.getContext();
		}
		return r;
	}


}

