package magic.ast;

import magic.RT;
import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.IPersistentSet;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Expression representing a constant.
 * 
 * @author Mike
 *
 */
public class Constant<T> extends BaseConstant<T> {

	public static final Constant<?> NULL = Constant.create(null);
	
	private final T value;
	
	public Constant(T value, IPersistentSet<Symbol> deps) {
		super((deps==null)?Sets.emptySet():deps);
		this.value=value;
	}
	
	@Override
	public Result<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		return new Result<T>(c,value);
	}

	public static <T> Constant<T> create(T v) {
		return new Constant<T>(v,null);
	}
	
	public static <T> Constant<T> create(T v, IPersistentSet<Symbol> deps) {
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
	


}
