package magic.ast;

import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.Symbol;

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
