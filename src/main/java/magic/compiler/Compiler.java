package magic.compiler;

import magic.ast.Node;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;

public class Compiler {

	@SuppressWarnings("unchecked")
	public static <T> Result<T> compile(Context context, Node<T> node) {
		return node.compile(context,(APersistentMap<Symbol, ?>) Maps.EMPTY);
	}
	
	public static <T> Result<T> compile(Context context, Object form) {
		Node<T> node=Analyser.analyse(context, form);
		return compile(context,node);
	}

	public static <T> Result<T> compile(Context c, String string) {
		APersistentVector<Object> forms=Reader.readAll(string);
		int n=forms.size();
		
		Result<T> r=new Result<>(c,null);
		for (int i=0; i<n; i++) {
			r=compile(r.getContext(),forms.get(i));
		}
		return r;
	}

}

