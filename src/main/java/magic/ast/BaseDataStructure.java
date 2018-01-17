package magic.ast;

import magic.compiler.EvalResult;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Abstract base class for data structure construction nodes (lists, sets, vectors etc.)
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class BaseDataStructure<T> extends Node<T> {
	/**
	 * Sub-expressions that represent data in this data structure
	 */
	protected final APersistentVector<Node<?>> exps;

	public BaseDataStructure(APersistentVector<Node<?>> exps, APersistentMap<Keyword,Object> meta) {
		super(meta);
		this.exps=exps;
	}
	
	/**
	 * For data structures, we product a list form equivalent to this data structure
	 * Overriden data structures (Vector etc.) must convert the list to the correct data structure representation
	 */
	@Override
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=exps.size();
		if (n==0) return EvalResult.create(context, PersistentList.EMPTY);
		
		Object[] rs=new Object[n];
		for (int i=0; i<n; i++) {
			EvalResult<Object> r=exps.get(i).evalQuoted(context, bindings, syntaxQuote);
			rs[i]=r.getValue();
			context=r.getContext();
		}
		APersistentList<Object> listResult=Lists.create(rs);
		return EvalResult.create(context, listResult);
	}
	
	@Override
	protected APersistentSet<Symbol> includeDependencies(APersistentSet<Symbol> deps) {
		for (Node<?> n: exps) {
			deps=deps.includeAll(n.getDependencies());
		}
		return deps;
	}
	
	@Override
	public final APersistentVector<Node<?>> getNodes() {
		return exps;
	}
}
