package magic.ast;

import magic.Keywords;
import magic.Symbols;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node which casts an expression to a given type.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Cast<T> extends BaseForm<T> {

	private final Type type;
	private final Node<?> exp;
	
	@SuppressWarnings("unchecked")
	public Cast(Type type, Node<?> exp, APersistentMap<Keyword,Object> meta) {
		super(Lists.of(Lookup.create(Symbols.CAST),Constant.create(type),exp), meta);
		this.exp=exp;
		this.type=type;
	}
	
	@Override
	public Cast<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Cast<T>(type,exp,meta);
	}
	
	public static <T> Cast<T> create(Type type, Node<?> exp, SourceInfo sourceInfo) {
		return new Cast<T>(type,exp,Maps.create(Keywords.SOURCE, sourceInfo));
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Cast<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		return create(type,fn.apply(exp),getSourceInfo());
	}

	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		EvalResult<?> r=exp.eval(context, bindings);
		Object result=r.getValue();
		if (type.checkInstance(result)) {
			return (EvalResult<T>) r;
		} else {
			throw new magic.Error("Cannot cast object of class "+result.getClass()+" to type "+type);
		}
	}
}
