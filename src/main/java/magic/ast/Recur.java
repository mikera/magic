package magic.ast;

import magic.Symbols;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node which recurs to the enclosing loop or function.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Recur<T> extends BaseForm<T> {

	private final APersistentVector<Node<?>> exps;
	
	public Recur(APersistentVector<Node<?>> exps, APersistentMap<Keyword,Object> meta) {
		super(Lists.cons(Lookup.create(Symbols.RECUR),Lists.coerce(exps)), meta);
		this.exps=exps;
	}
	
	@Override
	public Recur<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Recur<T>(exps,meta);
	}
	
	public static <T> Recur<T> create(APersistentVector<Node<?>> exps, APersistentMap<Keyword,Object> meta) {
		return new Recur<T>(exps,meta);
	}
	
	@Override
	public Type getType() {
		return Types.NONE;
	}

	@Override
	public Recur<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentVector<Node<?>> newExps=NodeFunctions.mapAll(exps, fn);
		if (newExps==exps) return this;
		return create(newExps,meta());
	}

	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		Object[] rs=new Object[n];
		for (int i=0; i<n; i++) {
			EvalResult<?> r=exps.get(i).eval(context, bindings);
			if (r.isRecurring()) throw new Error("Can't recur from within a recur in form: "+this);
			rs[i]=r.getValue();
			context=r.getContext();
		}
		return EvalResult.recurValues(context,rs);
	}
}
