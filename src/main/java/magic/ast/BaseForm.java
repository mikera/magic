package magic.ast;

import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.PersistentHashMap;
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

	protected APersistentList<Node<?>> nodes;

	/**
	 * Creates a Baseform with the given nodes. Includes dependencies from all source nodes by default
	 * @param nodes
	 * @param meta
	 */
	protected BaseForm(APersistentList<Node<?>> nodes, APersistentMap<Keyword,Object> meta) {
		super(meta);
		this.nodes=nodes;
	}
	
	protected BaseForm(APersistentList<Node<? extends Object>> nodes) {
		this (nodes,PersistentHashMap.empty());
	}
	
	@Override
	protected APersistentSet<Symbol> includeDependencies(APersistentSet<Symbol> deps) {
		for (Node<?> n: nodes) {
			deps=deps.includeAll(n.getDependencies());
		}
		return deps;
	}

	@Override
	public Node<?> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=nodes.size();
		if (n==0) return Constant.create(PersistentList.EMPTY);
		
		Node<?>[] rs=new Node[n];
		for (int i=0; i<n; i++) {
			rs[i]=nodes.get(i).evalQuoted(context, bindings, syntaxQuote);
		}
		ListForm listResult=ListForm.create(rs,getSourceInfo());
		return listResult;
	}

	@Override
	public APersistentList<Object> toForm() {
		return nodes.map(NodeFunctions.TO_FORM);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends T> optimise() {
		return (Node<T>) mapChildren(NodeFunctions.OPTIMISE);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return (Node<? extends T>) mapChildren(NodeFunctions.specialiseValues(bindings));
	}

	@Override
	public abstract String toString();
}
