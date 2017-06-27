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
import magic.lang.Context;
import magic.lang.Keywords;

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
	public static final Constant<Boolean> TRUE = Constant.create(Boolean.TRUE);
	public static final Constant<Boolean> FALSE = Constant.create(Boolean.FALSE);
	
	private final T value;
	private final Type type;
	
	private Constant(T value, APersistentMap<Keyword,Object> meta) {
		super(meta);
		this.value=value;
		this.type=RT.inferType(value);
	}
	
	@Override
	public Constant<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Constant<T>(value,meta);
	}
	
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		return new EvalResult<T>(c,value);
	}

	public static <T> Constant<T> create(T v) {
		return create(v,(SourceInfo)null);
	}

	public static <T> Constant<T> create(T o, SourceInfo sourceInfo) {
		return new Constant<T>(o,Maps.create(Keywords.SOURCE,sourceInfo));
	}
	
	public static <T> Constant<T> create(T v, APersistentSet<Symbol> deps) {
		return new Constant<T>(v,Maps.create(Keywords.DEPS,deps));
	}
	
	@Override
	public boolean isKeyword() {
		return value instanceof magic.data.Keyword;
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





}
