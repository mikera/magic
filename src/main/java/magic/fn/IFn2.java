package magic.fn;

import java.util.function.BiFunction;

@FunctionalInterface
public interface IFn2<R> extends IFixedFn<R>, BiFunction<Object,Object,R> {

	@Override
	public R apply(Object o1, Object o2);
	
	@Override
	public default int arity() {
		return 2;
	}
}
