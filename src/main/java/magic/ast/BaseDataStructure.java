package magic.ast;

import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Keyword;

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
	
	public final APersistentVector<Node<?>> getNodes() {
		return exps;
	}
}
