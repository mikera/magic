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
	 * Analyses a node in the given context. Performs type checking etc.
	 * @param context
	 * @param node
	 * @return
	 */
	private static Node<?> analyse(Context context, Node<?> node) {
		return node.analyse(context);
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
		node=Compiler.analyse(context,node);
		node=(Node<? super T>) node.optimise();
		// TODO: should specialise to context here?
		return (Node<T>)node;
	}


	/**
	 * Compiles and evaluates a node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<? super T> node) {
		return (EvalResult<T>) compile(context,node,(APersistentMap<Symbol, Object>) Maps.EMPTY);
		
	}
	 
	/**
	 * Compiles and evaluates a node in the given context. Performs expansion using the default expander
	 */
	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<? super T> node, APersistentMap<Symbol, Object> bindings) {
		Node<? super T> compiledNode=compileNode(context,node);
		EvalResult<T> result;
		try {
			result=(EvalResult<T>) compiledNode.eval(context,bindings);
		} catch (Throwable t) {
			throw new Error(t.getMessage()+"\n"+
		              "while evaluating: " + node,t); 
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
		ListForm forms=Reader.readAll(string);
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

