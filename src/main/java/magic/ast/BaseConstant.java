package magic.ast;

import magic.Type;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * Abstract base class for AST nodes representing constant expressions. 
 * 
 * Constant expressions always return the same value, independent of the context.
 * @author Mike
 *
 * @param <T>
 */
public abstract class BaseConstant<T> extends Node<T> {
	
	public BaseConstant(APersistentMap<Keyword,Object> meta) {
		super(meta);
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
	
	@Override
	public BaseConstant<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		// No child nodes
		return this;
	}
	
	/**
	 * Gets the Type of this constant
	 */
	@Override
	public abstract Type getType();
	
	@Override
	public Node<?> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		return this;
	}
	
	@Override
	public Object toForm() {
		return getValue();
	}

}
