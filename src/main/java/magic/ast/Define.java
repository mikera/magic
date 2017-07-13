package magic.ast;

import magic.Keywords;
import magic.Symbols;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node representing the action of defining a symbol in the current context
 * 
 * Currently the only valid way to make changes to the context, which occurs due to evaluation of
 * Def nodes during the compiler execution phase.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Define<T> extends BaseForm<T> {

	final Symbol sym;
	final Node<? extends T> exp;

	public Define(Symbol sym, Node<? extends T> exp, APersistentMap<Keyword,Object> meta) {
		super(PersistentList.of(Lookup.create(Symbols.DEF),Lookup.create(sym),exp),meta);
		this.sym=sym;
		this.exp=exp;
	}
	
	@Override
	public Define<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Define<T>(sym, exp,meta);
	}

	public static <T> Define<T> create(Symbol sym, Node<T> exp) {
		return create(sym,exp,Maps.empty());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Define<T> create(Symbol sym, Node<T> exp,APersistentMap<Keyword, Object> meta) {
		APersistentSet<Symbol> deps=(APersistentSet<Symbol>) meta.get(Keywords.DEPS);
		if (deps==null) deps=Sets.emptySet();
		deps=deps.includeAll(exp.getDependencies());
		meta=meta.assoc(Keywords.DEPS, deps);
		return new Define<T>(sym,exp,meta);
	}
	
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		Node<?> theExp=exp;
		Symbol sym=this.sym;
		if (!sym.isQualified()) {
			// add current namespace to symbol
			sym=Symbol.create(context.getCurrentNamespace(),sym.getName());
		}
		context=context.define(sym, theExp, bindings); 
		return new EvalResult<T>(context,null); // TODO: what should def return??
	}
	
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}

	@Override
	public Node<? extends T> optimise() {
		Node<? extends T> newExp=exp.optimise();
		return (exp==newExp)?this:create(sym,newExp);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Define<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<? extends T> newExp=(Node<? extends T>) fn.apply(exp);
		return (exp==newExp)?this:(Define<T>) create(sym,newExp);
	}

	
}
