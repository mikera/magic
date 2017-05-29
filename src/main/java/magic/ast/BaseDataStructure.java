package magic.ast;

import magic.compiler.SourceInfo;
import magic.data.APersistentSet;
import magic.data.Symbol;

public abstract class BaseDataStructure<T> extends Node<T> {

	public BaseDataStructure(APersistentSet<Symbol> deps, SourceInfo source) {
		super(deps, source);
	}

}
