package magic.expression;

import magic.data.PersistentHashMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Define<T> extends Expression<T> {

	final Symbol sym;
	final Expression<T> exp;

	public Define(Symbol sym, Expression<T> exp) {
		this.sym=sym;
		this.exp=exp;
	}

	@Override
	public T compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		throw new UnsupportedOperationException("Define only works in compile mode?");
	}

	public static <T> Define<T> create(Symbol sym, Expression<T> exp) {
		// TODO Auto-generated method stub
		return new Define<T>(sym,exp);
	}

}
