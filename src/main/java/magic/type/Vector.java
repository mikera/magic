package magic.type;

import magic.Type;
import magic.data.APersistentVector;

/**
 * Type representing a sized persistent sequence 
 * 
 * Optional parameters
 *   size: may define a vector of a given size
 *   type: may define a vector with elements of a specific type
 * @author Mike
 *
 */
@SuppressWarnings("rawtypes")
public class Vector extends JavaType<APersistentVector>{

	/**
	 * Size of vector, or negative if any size admitted
	 */
	private final int size;
	
	/**
	 * Type of elements, or null if any type admitted
	 */
	private final Type elementType;

	public Vector(Type type, int n) {
		super(APersistentVector.class);
		this.size=n;
		this.elementType=type;
	}
	
	public static Vector create(int n) {
		return new Vector(null,n);
	}
	
	public Type getElementType() {
		return (elementType==null)?Anything.INSTANCE:elementType;
	}
	
	@Override
	public boolean checkInstance(Object o) {
		if (o==null) return false;
		if (!(o instanceof APersistentVector)) return false;
		APersistentVector<?> v=(APersistentVector<?>) o;
		int vsize=v.size();
		if ((size>=0)&&(vsize!=size)) return false;
		
		if (elementType==null) return true;

		// check if element types fully contained
		Type vtype=v.getType();
		if (vtype instanceof Vector) {
			Vector vectorType=(Vector)vtype;
			if (elementType.contains(vectorType)) return true;
		}
		
		// fallback: check individual elements
		for (int i=0; i<vsize; i++) {
			if (!elementType.checkInstance(v.get(i))) return false;
		}
		return true;
	}

}
