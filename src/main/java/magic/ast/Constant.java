package magic.ast;

import magic.RT;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;
import magic.type.JavaType;

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
	
	public Constant(T value, APersistentSet<Symbol> deps) {
		super((deps==null)?Sets.emptySet():deps);
		this.value=value;
	}
	
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		return new EvalResult<T>(c,value);
	}

	public static <T> Constant<T> create(T v) {
		return new Constant<T>(v,null);
	}
	
	public static <T> Constant<T> create(T v, APersistentSet<Symbol> deps) {
		return new Constant<T>(v,deps);
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "(Constant "+RT.print(value)+")";
	}
	
	/**
	 * Gets the Java Type of this constant, or Types.NULL if null
	 */
	@Override
	public Type getType() {
		if (value==null) return Types.NULL;
		return (Type) JavaType.create(value);
	}

}
