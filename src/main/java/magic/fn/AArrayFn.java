package magic.fn;

/**
 * Abstract base class for functions that expect arguments passed in an array
 * @author Mike
 *
 * @param <T>
 */
public abstract class AArrayFn<R> extends AFn<R>{

	@Override
	public R apply() {
		return applyToArray();
	}
	
	@Override
	public R apply(Object o) {
		return applyToArray(o);
	}
	
	@Override
	public R apply(Object o1, Object o2) {
		return applyToArray(o1,o2);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3) {
		return applyToArray(o1,o2,o3);
	}

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4) {
		return applyToArray(o1,o2,o3,o4);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5) {
		return applyToArray(o1,o2,o3,o4,o5);
	}

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
		return applyToArray(o1,o2,o3,o4,o5,o6);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7);
	}

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15,o16);
	}

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15,o16,o17);
	}

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17, Object o18) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15,o16,o17,o18);
	}

	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17, Object o18, Object o19) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15,o16,o17,o18,o19);
	}
	
	@Override
	public R apply(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9, Object o10, Object o11, Object o12, Object o13, Object o14, Object o15, Object o16, Object o17, Object o18, Object o19, Object o20) {
		return applyToArray(o1,o2,o3,o4,o5,o6,o7,o8,o9,o10,o11,o12,o13,o14,o15,o16,o17,o18,o19,o20);
	}

	@Override
	public abstract R applyToArray(Object... a);

}
