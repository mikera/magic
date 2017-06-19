package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Keywords;

/**
 * AST node representing the action of defining a symbol in the current context
 * 
 * Currently the only valid way to make changes to the context, which occurs due to evaluation of
 * Def nodes during the compilation phase.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Define<T> extends BaseForm<T> {

	final Symbol sym;
	final Node<? extends T> exp;

	public Define(Symbol sym, Node<? extends T> exp, APersistentMap<Keyword,Object> meta) {
		super(PersistentList.of(Constant.create(sym),exp),meta);
		this.sym=sym;
		this.exp=exp;
	}
	
	@Override
	public Define<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Define<T>(sym, exp,meta);
	}

	public static <T> Define<T> create(Symbol sym, Node<T> exp) {
		return create(sym,exp,null);
	}
	
	public static <T> Define<T> create(Symbol sym, Node<T> exp,SourceInfo source) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE,source);
		meta=meta.assoc(Keywords.DEPS, exp.getDependencies());
		return new Define<T>(sym,exp,meta);
	}
	
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		Node<?> theExp=exp;
		// use evalQuoted to expand any unquotes... TODO is this a good idea?
		//theExp=theExp.evalQuoted(context,bindings,true);
		// theExp=theExp.specialiseValues(bindings);
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

	@Override
	public String toString() {
		return "(def "+sym+" "+RT.toString(exp)+")";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Define<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<? extends T> newExp=(Node<? extends T>) fn.apply(exp);
		return (exp==newExp)?this:(Define<T>) create(sym,newExp);
	}

	
}
