package magic.ast;

import magic.RT;
import magic.compiler.Result;
import magic.data.IPersistentSet;
import magic.data.PersistentHashMap;
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

	private final T value;
	
	public Constant(T value, IPersistentSet<Symbol> deps) {
		super((deps==null)?Sets.emptySet():deps);
		this.value=value;
	}
	
	@Override
	public T compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		return value;
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

	@Override
	public Result<T> compile(Context context) {
		// no change to context, returns pure value
		return new Result<>(context,value);
	}
}
