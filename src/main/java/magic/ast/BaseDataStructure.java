package magic.ast;

import magic.compiler.SourceInfo;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Symbol;

public abstract class BaseDataStructure<T> extends Node<T> {
	protected final APersistentVector<Node<?>> exps;

	public BaseDataStructure(APersistentVector<Node<?>> exps, APersistentSet<Symbol> deps,
			SourceInfo source) {
		super(deps, source);
		this.exps=exps;
	}
}
