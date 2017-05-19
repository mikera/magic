package magic.ast;

import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;

public abstract class BaseForm<T> extends Node<T> {

	private APersistentList<Node<?>> form;

	public BaseForm(APersistentList<Node<?>> form, APersistentSet<Symbol> deps, SourceInfo source) {
		super(deps, source);
		this.form=form;
	}
	
	@Override
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=form.size();
		if (n==0) return new EvalResult<Object>(context,PersistentList.EMPTY);
		
		Object[] rs=new Object[n];
		for (int i=0; i<n; i++) {
			rs[i]=form.get(i).evalQuoted(context, bindings, syntaxQuote);
		}
		PersistentList<Object> listResult=PersistentList.wrap(rs);
		return new EvalResult<Object>(context,listResult);
	}

}
