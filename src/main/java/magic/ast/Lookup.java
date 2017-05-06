package magic.ast;

import magic.compiler.Reader;
import magic.compiler.Result;
import magic.data.PersistentHashMap;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Expression node for looking up a symbol in a context
 * @author Mike
 *
 * @param <T>
 */
public class Lookup<T> extends Node<T> {

	private final Symbol sym;
	
	private Lookup(Symbol sym) {
		super(sym.symbolSet());
		this.sym=sym;
	}
	
	public static <T> Lookup<T> create(Symbol sym) {
		return new Lookup<T>(sym);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		if (bindings.containsKey(sym)) return (T) bindings.get(sym);
		return (T)(c.getValue(sym));
	}

	public static <T> Lookup<T> create(String sym) {
		return create(Reader.readSymbol(sym));
	}
	
	@Override
	public Result<T> compile(Context context) {
		// no change to context, returns pure value
		return new Result<>(context,context.getValue(sym));
	}

}
