package magic.ast;

import magic.data.IPersistentSet;
import magic.data.Symbol;

/**
 * Base class for constant expressions. Constant expressions always return the same value,
 * independent of the context.
 * @author Mike
 *
 * @param <T>
 */
public abstract class BaseConstant<T> extends Node<T> {
	
	public BaseConstant(IPersistentSet<Symbol> deps) {
		super(deps);
	}

	@Override
	public abstract T getValue();
}
