package magic.compiler;

import magic.RT;
import magic.ast.Constant;
import magic.ast.List;
import magic.ast.Node;
import magic.ast.Vector;
import magic.data.APersistentList;
import magic.data.APersistentSequence;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.lang.Context;

/**
 * Magic code analyser
 * 
 * Responsible for converting forms into AST representations
 * 
 * @author Mike
 *
 */
public class Analyser {

	/**
	 * Analyses a form in an empty context. Useful mainly for debug / test purposes
	 * @param form
	 * @return
	 */
	public static <T> Node<T> analyse(Object form) {
		return analyse(RT.INITIAL_CONTEXT,form);
	}
	
	/**
	 * Analyses a list of forms, producing an array of nodes
	 * @param c
	 * @param forms
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> Node<T>[] analyseAll(Context c, APersistentSequence<? extends Object> forms) {
		int n=forms.size();
		Node<?>[] exps=new Node<?>[n];
		for (int i=0; i<n; i++) {
			exps[i]=analyse(c,forms.get(i));
		}
		return (Node<T>[]) exps;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Node<T> analyse(Context c, Object form) {
		if (form==null) return (Node<T>) Constant.NULL;
		if (form instanceof APersistentList) return analyseList(c,(APersistentList<Object>)form);
		if (form instanceof APersistentVector) return analyseVector(c,(APersistentVector<Object>)form);
		// TODO:
		//if (form instanceof APersistentSet) return analyseSet(c,(APersistentSet<Object>)form);
		//if (form instanceof APersistentMap) return analyseMap(c,(APersistentMap<Object,Object>)form);

		// fall through handles constant literals, keywords etc
		return (Node<T>) Constant.create(form);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseList(Context c, APersistentList<? extends Object> form) {
		int n=form.size();
		if (n==0) return (Node<T>) Constant.create(Lists.EMPTY);
		
		return (Node<T>) List.create(analyseAll(c,form));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseVector(Context c, APersistentVector<? extends Object> form) {
		int n=form.size();
		if (n==0) return (Node<T>) Constant.create(Lists.EMPTY);
		
		return (Node<T>) Vector.create(analyseAll(c,form));
	}

	/**
	 * Expands a form in a given context
	 * @param context
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Node<T> expand(Context context, Node<?> node) {
		Expander ex=Expanders.INITAL_EXPANDER;
		return (Node<T>) ex.expand(context, node, ex);
	}


}
