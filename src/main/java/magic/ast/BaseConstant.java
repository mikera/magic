package magic.ast;

import magic.Type;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.IPersistentSet;
import magic.data.Symbol;
import magic.lang.Context;
import magic.type.JavaType;

/**
 * Abstract base class for AST nodes representing constant expressions. 
 * 
 * Constant expressions always return the same value, independent of the context.
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
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		// no change to context, returns pure value
		return new EvalResult<>(context,getValue());
	}
	
	@Override
	public Node<T> optimise() {
		return this;
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return this;
	}
	
	/**
	 * Gets the Java Type of this constant
	 */
	@Override
	public Type getType() {
		return JavaType.create(getValue());
	}
}
