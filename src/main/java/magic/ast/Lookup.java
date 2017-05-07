package magic.ast;

import magic.compiler.Reader;
import magic.compiler.Result;
import magic.data.APersistentMap;
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
	public Result<T> compile(Context c,APersistentMap<Symbol,?> bindings) {
		T val = (bindings.containsKey(sym))?(T)bindings.get(sym):c.getValue(sym);
		return new Result<T>(c,val);
	}

	public static <T> Lookup<T> create(String sym) {
		return create(Reader.readSymbol(sym));
	}

}
