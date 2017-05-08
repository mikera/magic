package magic.compiler;

import magic.ast.Node;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;

public class Compiler {

	@SuppressWarnings("unchecked")
	public static <T> EvalResult<T> compile(Context context, Node<T> node) {
		return node.eval(context,(APersistentMap<Symbol, Object>) Maps.EMPTY);
	}
	
	/*
	 * Compiles a form in the given context. Performs expansion using the default expander
	 */
	public static <T> EvalResult<T> compile(Context context, Object form) {
		form=Analyser.expand(context,form);
		Node<T> node=Analyser.analyse(context, form);
		return compile(context,node);
	}

	public static <T> EvalResult<T> compile(Context c, String string) {
		APersistentVector<Object> forms=Reader.readAll(string);
		int n=forms.size();
		
		EvalResult<T> r=new EvalResult<>(c,null);
		for (int i=0; i<n; i++) {
			r=compile(r.getContext(),forms.get(i));
		}
		return r;
	}

}

