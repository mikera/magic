package magic.fn;

/**
 * General magic function interface
 * @author Mike
 *
 */
@FunctionalInterface
public interface IFn<R> {
	public default R apply() {
		return throwArity(0);
	}
	
	public default R apply(Object o1) {
		return throwArity(1);
	}
	
	public default R apply(Object o1, Object o2) {
		return throwArity(2);
	}
	
	public default R apply(Object o1, Object o2, Object o3) {
		return throwArity(3);
	}

	public default R apply(Object o1, Object o2, Object o3, Object o4) {
		return throwArity(4);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5) {
		return throwArity(5);
	}

	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
		return throwArity(6);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
		return throwArity(7);
	}

	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
		return throwArity(8);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
		return throwArity(9);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10) {
		return throwArity(10);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11) {
		return throwArity(11);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12) {
		return throwArity(12);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13) {
		return throwArity(13);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14) {
		return throwArity(14);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15) {
		return throwArity(15);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16) {
		return throwArity(16);
	}

	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17) {
		return throwArity(17);
	}

	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17, Object o18) {
		return throwArity(18);
	}

	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17, Object o18, Object o19) {
		return throwArity(19);
	}
	
	public default R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17, Object o18, Object o19, Object o20) {
		return throwArity(20);
	}

	public R applyTo(Object o);
	
	public default R throwArity(int arity) {
		throw new ArityException(arity(), arity);
	}
	
	public default int arity() {
		throw new ArityException("No arity defined");
	}
}
