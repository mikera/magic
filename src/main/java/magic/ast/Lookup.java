package magic.ast;

import magic.compiler.Reader;
import magic.compiler.SourceInfo;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Expression node for looking up a symbol in a context
 * 
 * @author Mike
 * @param <T>
 */
public class Lookup<T> extends Node<T> {

	private final Symbol sym;
	
	private Lookup(Symbol sym, SourceInfo source) {
		super(sym.symbolSet(),source);
		this.sym=sym;
	}
	
	public static <T> Lookup<T> create(Symbol sym) {
		return create(sym,null);
	}
	
	public static <T> Lookup<T> create(Symbol sym, SourceInfo source) {
		return new Lookup<T>(sym,source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		T val = (bindings.containsKey(sym))?(T)bindings.get(sym):c.getValue(sym);
		return new EvalResult<T>(c,val);
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
	
	public Symbol getSymbol() {
		return sym;
	}
	
	@Override
	public Node<T> optimise() {
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
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		if (syntaxQuote&&(!sym.isQualified())) {
			String ns=context.getCurrentNamespace();
			Symbol qualSym=Symbol.create(ns, sym.getName());
			return new EvalResult<Object>(context,qualSym);
		} 
		return new EvalResult<Object>(context,sym);
	}

}
