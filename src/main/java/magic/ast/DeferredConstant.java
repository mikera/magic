package magic.ast;

import magic.RT;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Maps;
import magic.data.Symbol;
import magic.fn.IFn0;
import magic.lang.Context;
import magic.lang.Keywords;

/**
 * AST node representing a constant value, which is computed an a deferred basis when it is first requested
 * 
 * May still have symbolic dependencies, e.g. when it is constructed from expansion and optimisation of another form/node
 * 
 * @author Mike
 *
 */
public class DeferredConstant<T> extends BaseConstant<T> {

	private final IFn0<T> fn;
	private final Type type;
	private boolean computed=false;
	private T value=null;
	
	public DeferredConstant(IFn0<T> fn, APersistentMap<Keyword,Object> meta) {
		super(meta);
		this.fn=fn;
		this.type=fn.getReturnType();
	}
	
	@Override
	public DeferredConstant<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new DeferredConstant<T>(fn,meta);
	}
	
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		return new EvalResult<T>(c,value);
	}

	public static <T> DeferredConstant<T> create(IFn0<T> fn, APersistentSet<Symbol> deps, SourceInfo sourceInfo) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE,sourceInfo);
		meta=meta.assoc(Keywords.DEPS, deps);
		return new DeferredConstant<T>(fn,meta);
	}
	
	public static <T> DeferredConstant<T> create(IFn0<T> fn) {
		return new DeferredConstant<T>(fn,Maps.empty());
	}
	
	@Override
	public T getValue() {
		if (computed) return value;
		return initialiseValue();
	}
	
	private synchronized T initialiseValue() {
		T v=fn.apply();
		value=v;
		computed=true;
		return v;
	}
	
	@Override
	public Object toForm() {
		return getValue();
	}
	
	@Override
	public String toString() {
		return RT.print(getValue());
	}
	
	/**
	 * Gets the Type of this constant, or Types.NULL if null
	 */
	@Override
	public Type getType() {
		return type;
	}

	


}
