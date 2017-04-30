package magic.expression;

import magic.compiler.Parser;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Expression node for looking up a symbol in a context
 * @author Mike
 *
 * @param <T>
 */
public class Lookup<T> extends Expression<T> {

	private final Symbol sym;
	
	private Lookup(Symbol sym) {
		this.sym=sym;
	}
	
	public static <T> Lookup<T> create(Symbol sym) {
		return new Lookup<T>(sym);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T compute(Context c) {
		return (T)(c.getValue(sym));
	}

	public static <T> Lookup<T> create(String sym) {
		return create(Parser.parseSymbol(sym));
	}

}
