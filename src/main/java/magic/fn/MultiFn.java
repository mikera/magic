package magic.fn;

import java.util.Arrays;

import magic.RT;
import magic.Type;
import magic.data.Lists;

/**
 * Function implementation supporting dynamic dispatch across multiple functions
 * 
 * @author Mike
 *
 */
public class MultiFn<T> extends AArrayFn<T> {

	private final AFn<?>[] fns;
	private final int minArity;
	private final int n;
	private final Type type;
	
	public MultiFn(AFn<?>[] functions) {
		this.fns=functions;
		n=functions.length;
		
		Type type=fns[0].getType();
		int a=fns[0].arity();
		for (int i=1; i<n; i++) {
			AFn<?> f=fns[i];
			int fa=f.arity();
			if (fa<a) a=fa;
			type=type.union(f.getType());
		}
		this.type=type;
		this.minArity=a;
	}

	public static <T> MultiFn<T> create(AFn<?>... functions) {
		return new MultiFn<T>(functions);
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public int arity() {
		return minArity;
	}

	@Override
	public boolean hasArity(int arity) {
		if (arity<minArity) return false;
		for (int i=0; i<n; i++) {
			if (fns[i].hasArity(arity)) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T applyToArray(Object... a) {
		for (int i=0; i<n; i++) {
			AFn<?> f=fns[i];
			if (f.acceptsArgs(a)) {
				return (T) f.applyToArray(a);
			}
		}
		throw new Error("Function not applicable for objects of types " +RT.printTypes(a));
	}

	@Override
	public String toString() {
		return super.toString()+"\n("+RT.toString(Lists.coerce(Arrays.asList(fns)), " \n")+")";
	}
}
