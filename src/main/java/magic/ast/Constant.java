package magic.ast;

import magic.RT;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Sets;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node representing a constant value.
 * 
 * May still have symbolic dependencies, e.g. when it is constructed from expansion and optimisation of another form/node
 * 
 * @author Mike
 *
 */
public class Constant<T> extends BaseConstant<T> {

	public static final Constant<?> NULL = Constant.create(null);
	
	private final T value;
	private final Type type;
	
	public Constant(T value, APersistentSet<Symbol> deps, SourceInfo source) {
		super((deps==null)?Sets.emptySet():deps,source);
		this.value=value;
		this.type=RT.inferType(value);
	}
	
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		return new EvalResult<T>(c,value);
	}

	public static <T> Constant<T> create(T v) {
		return create(v,(SourceInfo)null);
	}

	public static <T> Constant<T> create(T o, SourceInfo sourceInfo) {
		return new Constant<T>(o,null,sourceInfo);
	}
	
	public static <T> Constant<T> create(T v, APersistentSet<Symbol> deps) {
		return new Constant<T>(v,deps,null);
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public Object toForm() {
		return value;
	}
	
	@Override
	public String toString() {
		return RT.print(value);
	}
	
	/**
	 * Gets the Type of this constant, or Types.NULL if null
	 */
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Constant<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		// No child nodes
		return this;
	}




}
