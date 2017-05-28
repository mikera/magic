package magic.ast;

import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Abstract base class for regular forms stored as lists.
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class BaseForm<T> extends Node<T> {

	protected APersistentList<Node<? extends Object>> nodes;

	public BaseForm(APersistentList<Node<? extends Object>> aPersistentList, APersistentSet<Symbol> deps, SourceInfo source) {
		super(deps, source);
		this.nodes=aPersistentList;
	}
	
	@Override
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=nodes.size();
		if (n==0) return new EvalResult<Object>(context,PersistentList.EMPTY);
		
		Object[] rs=new Object[n];
		for (int i=0; i<n; i++) {
			rs[i]=nodes.get(i).evalQuoted(context, bindings, syntaxQuote);
		}
		PersistentList<Object> listResult=PersistentList.wrap(rs);
		return new EvalResult<Object>(context,listResult);
	}

	@Override
	public APersistentList<Object> toForm() {
		return nodes.map(Nodes.TO_FORM);
	}
}
