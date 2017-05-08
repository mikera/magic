package magic.ast;

import magic.Type;
import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.IPersistentSet;
import magic.data.Symbol;
import magic.lang.Context;
import magic.type.JavaType;

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
	
	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		// no change to context, returns pure value
		return new Result<>(context,getValue());
	}
	
	@Override
	public Node<T> optimise() {
		return this;
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return this;
	}
	
	@Override
	public Type getType() {
		return JavaType.create(getValue());
	}
}
