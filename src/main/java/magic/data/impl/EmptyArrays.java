package magic.data.impl;

import magic.Type;

/**
 * EMPTY array type Singletons
 */
public class EmptyArrays {
	public static final Object[] EMPTY_OBJECTS=new Object[0];
	public static final float[] EMPTY_FLOATS=new float[0];
	public static final double[] EMPTY_DOUBLES=new double[0];
	public static final int[] EMPTY_INTS=new int[0];
	public static final long[] EMPTY_LONGS=new long[0];
	public static final char[] EMPTY_CHARS=new char[0];
	public static final short[] EMPTY_SHORTS=new short[0];
	public static final byte[] EMPTY_BYTES=new byte[0];
	public static final boolean[] EMPTY_BOOLEANS=new boolean[0];
	public static final String[] EMPTY_STRINGS=new String[0];

	public static Object getEMPTYArray(Type dt) {
		Class<?> jc=dt.getJavaClass();

		if (!dt.isPrimitive()) return EMPTY_OBJECTS;
		
		if (jc==Byte.class) return EMPTY_BYTES;
		if (jc==Short.class) return EMPTY_SHORTS;
		if (jc==Integer.class) return EMPTY_INTS;
		if (jc==Long.class) return EMPTY_LONGS;
		if (jc==Boolean.class) return EMPTY_BOOLEANS;
		if (jc==Character.class) return EMPTY_CHARS;
		if (jc==Float.class) return EMPTY_FLOATS;
		if (jc==Double.class) return EMPTY_DOUBLES;
		
		// nothing recognised?
		throw new Error("Datatype not recognised: "+dt);
	}

}
