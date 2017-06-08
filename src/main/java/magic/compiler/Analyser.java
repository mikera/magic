package magic.compiler;

import magic.ast.Constant;
import magic.ast.ListForm;
import magic.ast.Lookup;
import magic.ast.Node;
import magic.ast.Vector;
import magic.data.APersistentList;
import magic.data.APersistentSequence;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.data.Symbol;

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
	 * Analyses a list of form data structures, producing an array of nodes
	 * @param c
	 * @param forms
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> Node<T>[] analyseAll(APersistentSequence<? extends Object> forms) {
		int n=forms.size();
		Node<?>[] exps=new Node<?>[n];
		for (int i=0; i<n; i++) {
			exps[i]=analyse(forms.get(i));
		}
		return (Node<T>[]) exps;
	}
	
	/**
	 * Analyses a form data structure, producing an array of nodes
	 * @param c
	 * @param forms
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Node<T> analyse(Object form) {
		if (form==null) return (Node<T>) Constant.NULL;
		if (form instanceof APersistentList) return analyseList((APersistentList<Object>)form);
		if (form instanceof APersistentVector) return analyseVector((APersistentVector<Object>)form);
		// TODO:
		//if (form instanceof APersistentSet) return analyseSet(c,(APersistentSet<Object>)form);
		//if (form instanceof APersistentMap) return analyseMap(c,(APersistentMap<Object,Object>)form);

		if (form instanceof Symbol) return Lookup.create((Symbol)form);
		
		// fall through handles constant literals, keywords etc
		return (Node<T>) Constant.create(form);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseList(APersistentList<? extends Object> form) {
		int n=form.size();
		if (n==0) return (Node<T>) Constant.create(Lists.EMPTY);
		
		return (Node<T>) ListForm.create(analyseAll(form));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseVector(APersistentVector<? extends Object> form) {
		int n=form.size();
		if (n==0) return (Node<T>) Constant.create(Lists.EMPTY);
		
		return (Node<T>) Vector.create(analyseAll(form));
	}


}
