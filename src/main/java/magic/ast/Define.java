package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;

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

	public Define(Symbol sym, Node<T> exp, SourceInfo source) {
		super(PersistentList.of(Constant.create(sym),exp),exp.getDependencies(),source);
		this.sym=sym;
		this.exp=exp;
	}

	public static <T> Define<T> create(Symbol sym, Node<T> exp) {
		return new Define<T>(sym,exp,null);
	}
	
	public static <T> Define<T> create(Symbol sym, Node<T> exp,SourceInfo source) {
		return new Define<T>(sym,exp,null);
	}
	
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		Node<? extends T> specExp=exp.specialiseValues(bindings);
		context=context.define(sym, specExp); 
		return new EvalResult<T>(context,null); // TODO: what should def return??
	}
	
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<? extends T> newExp=exp.specialiseValues(bindings);
		return (exp==newExp)?this:create(sym,newExp);
	}

	@Override
	public Node<? extends T> optimise() {
		Node<? extends T> newExp=exp.optimise();
		return (exp==newExp)?this:create(sym,newExp);
	}

	@Override
	public String toString() {
		return "(Def "+sym+" "+RT.toString(exp)+")";
	}
}
