package magic.fn;

import java.util.List;

import magic.Type;
import magic.Types;

/**
 * General magic function interface
 * @author Mike
 * @param R return type of the function
 *
 */
public interface IFn<R> {
	public default R apply() {
		return throwArity(0);
	}
	
	public default R apply(Object o) {
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

	public default R applyTo(Object o) {
		if (o instanceof Object[]) {
			return applyToArray((Object[])o);
		} else if (o instanceof List<?>) {
			return applyToArray(((List<?>)o).toArray());
		}
		throw new RuntimeException("IFn.applyTo() not supported for class: "+o.getClass());
	}
	
	public default R applyToArray(Object... a) {
		int n=a.length;
		switch (a.length) {
		case 0:  return apply();
		case 1:  return apply(a[0]);
		case 2:  return apply(a[0],a[1]);
		case 3:  return apply(a[0],a[1],a[2]);
		case 4:  return apply(a[0],a[1],a[2],a[3]);
		case 5:  return apply(a[0],a[1],a[2],a[3],a[4]);
		case 6:  return apply(a[0],a[1],a[2],a[3],a[4],a[5]);
		case 7:  return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6]);
		case 8:  return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7]);
		case 9:  return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8]);
		case 10: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9]);
		case 11: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10]);
		case 12: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11]);
		case 13: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12]);
		case 14: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13]);
		case 15: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14]);
		case 16: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15]);
		case 17: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16]);
		case 18: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17]);
		case 19: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18]);
		case 20: return apply(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18],a[19]);
		default: return throwArity(n);
		}
	}
	
	public default R throwArity(int arity) {
		throw new ArityException(arity(), arity);
	}
	
	public default boolean isVariadic() {
		return true;
	}
	
	/**
	 * Gets the arity of the function, or the minimum permissible arity if variadic
	 * @return
	 */
	public int arity();
	
	public default Type getReturnType() {
		return Types.ANYTHING;
	}
	
	public default Type getParamType(int i) {
		return Types.ANYTHING;
	}

	public boolean hasArity(int i);
}
