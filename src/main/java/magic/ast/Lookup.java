package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.compiler.Reader;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Keywords;

/**
 * Expression node for looking up a symbol in a context
 * 
 * @author Mike
 * @param <T>
 */
public class Lookup<T> extends Node<T> {

	private final Symbol sym;
	
	private Lookup(Symbol sym, APersistentMap<Keyword, Object> meta) {
		super(meta);
		this.sym=sym;
	}
	
	@Override
	public Lookup<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Lookup<T>(sym,meta);
	}
	
	public static <T> Lookup<T> create(Symbol sym) {
		return create(sym,null);
	}
	
	public static <T> Lookup<T> create(Symbol sym, SourceInfo source) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE,source);
		meta=meta.assoc(Keywords.DEPS, sym.symbolSet());
		return new Lookup<T>(sym,meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		if (bindings.containsKey(sym)) return new EvalResult<T>(c,(T) bindings.get(sym));
		return new EvalResult<T>(c,RT.resolve(c,sym));
	}

	public static <T> Lookup<T> create(String sym) {
		return create(Reader.readSymbol(sym));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		if (bindings.containsKey(sym)) {
			return (Node<T>) Constant.create(bindings.get(sym));
		}
		return this;
	}
	
	@Override
	public Symbol getSymbol() {
		return sym;
	}
	
	@Override
	public boolean isSymbol() {
		return true;
	}
	
	@Override
	public Node<T> optimise() {
		return this;
	}
	
	@Override
	public Lookup<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		return this;
	}

	@Override
	public String toString() {
		// return "(Lookup "+sym+")";
		return sym.toString();
	}
	
	@Override
	public Symbol toForm() {
		return sym;
	}

	@Override
	public Node<?> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		return this;
	}



}
