package magic.ast;

import magic.Symbols;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node which returns the value of an expression to the enclosing context.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Return<T> extends BaseForm<T> {

	private final Node<?> exp;
	
	@SuppressWarnings("unchecked")
	public Return(Node<?> exp, APersistentMap<Keyword,Object> meta) {
		super(Lists.of(Lookup.create(Symbols.RETURN),exp), meta);
		this.exp=exp;
	}
	
	@Override
	public Return<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Return<T>(exp,meta);
	}
	
	public static <T> Return<T> create(Node<?> exp, APersistentMap<Keyword,Object> meta) {
		return new Return<T>(exp,meta);
	}
	
	@Override
	public Type getType() {
		return Types.NONE;
	}

	@Override
	public Return<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		return create(fn.apply(exp),meta());
	}

	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		EvalResult<?> r=exp.eval(context, bindings);
		if (r.isEscaping()) throw new Error("Can't return from within a return in form: "+this);
		return EvalResult.returnValue(r.getContext(),r.getValue());
	}
	
	@Override
	public String toString() {
		return "(RETURN "+exp+")";
	}
}
