package magic.ast;

import magic.Keywords;
import magic.RT;
import magic.compiler.AnalysisContext;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

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
	protected APersistentSet<Symbol> includeDependencies(APersistentSet<Symbol> deps) {
		return deps.include(sym);
	}

	@Override
	public Lookup<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Lookup<T>(sym,meta);
	}
	
	public static <T> Lookup<T> create(Symbol sym) {
		return create(sym,Maps.empty());
	}
	
	public static <T> Lookup<T> create(Symbol sym, APersistentMap<Keyword, Object> meta) {
		meta=RT.addMetaDependency(meta,sym);
		Lookup<T> lookup= new Lookup<T>(sym,meta);
		return lookup;
	}
	
	public static <T> Lookup<T> create(Symbol sym, SourceInfo si) {
		return create(sym,Maps.create(Keywords.SOURCE, si));
	}

	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		if (bindings.containsKey(sym)) return new EvalResult<T>(c,(T) bindings.get(sym));
		return new EvalResult<T>(c,RT.resolve(c,sym));
	}

	public static <T> Lookup<T> create(String sym) {
		return create(Symbol.create(sym));
	}
	
	@Override
	public Node<?> analyse(AnalysisContext context) {
		Symbol rSym=context.resolveSym(sym);
//		Node<?> node=context.getNode(rSym);
//		if (node==null) {
//			// TODO: what about unresolved dependencies?
//			// TODO: what about class names?
//			// throw new Error("Analysis error: Symbol "+sym+" cannot be resolved");
//		} 
		if (sym==rSym) {
			// just need to update the dependency
			return this.withDependency(rSym);
		} else {
			// need a new Lookup with correct symbol
			return create(rSym,meta()).withDependency(rSym);
		}
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
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		// just return the symbol
		return EvalResult.create(context, sym);
	}



}
