package magic.fn;

import java.util.function.BiFunction;

@FunctionalInterface
public interface IFn2<R> extends IFn<R>, BiFunction<Object,Object,R> {

	public R apply(Object o1, Object o2);
	
	public default int arity() {
		return 2;
	}
}
