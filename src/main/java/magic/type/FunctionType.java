package magic.type;

import magic.Type;
import magic.Types;
import magic.fn.IFn;

/**
 * Type representing a Magic function featuring:
 * - A single return type
 * - A fixed number of parameters with specified types
 * - Optional variadic parmameters of a specified type
 * 
 * @author Mike
 *
 */
public class FunctionType extends AFunctionType {

	private final Type returnType;
	private final Type[] paramTypes; // types of fixed parameters
	private final Type variadicType; // type of all variadic parameters after minArity
	private final int minArity;
	private final boolean variadic;
	
	private FunctionType(Type returnType) {
		this(returnType,Type.EMPTY_TYPE_ARRAY,Types.ANYTHING);
	}
	
	private FunctionType(Type returnType, Type[] paramTypes, Type variadicType) {
		this.returnType=returnType;
		this.paramTypes=paramTypes;
		this.variadicType=variadicType;
		this.variadic=(variadicType!=null);
		this.minArity=paramTypes.length;
	}
	
	public static FunctionType create(Type returnType, Type... types) {
		return create(returnType,types,null);
	}
	
	public static FunctionType create(Type returnType, Type[] ptypes,Type variadicType) {
		return new FunctionType(returnType,ptypes,variadicType);
	}
	
	public static FunctionType createMultiArity(Type returnType) {
		return createMultiArity(returnType,Types.ANYTHING);
	}
	
	public static FunctionType createMultiArity(Type returnType,Type variadicType) {
		Type[] ptypes=new Type[0];
		return new FunctionType(returnType,ptypes,variadicType);
	}
	
	public boolean hasArity(int n) {
		if (variadic) {
			return n>=minArity;
		} else {
			return n==minArity;
		}
	}
	
	@Override
	public Type getReturnType() {
		return returnType;
	}

	@Override
	public boolean checkInstance(Object o) {
		// TODO: check arity compatibility?
		if (o instanceof IFn) {
			IFn<?> fn=(IFn<?>)o;
			if (!returnType.contains(fn.getReturnType())) return false; // covariance on return type
			for (int i=0; i<minArity; i++) {
				if (!fn.getParamType(i).contains(paramTypes[i])) return false; // contravariance on parameter type				
			}
			return true;
		} else {
			// not a Magic function, so return false
			return false;			
		}
	}

	@Override
	public Class<?> getJavaClass() {
		return IFn.class;
	}

	@Override
	public boolean contains(Type t) {	
		if (t instanceof FunctionType) {
			return contains((FunctionType)t);
		} else {
			return false;
		}
	}
	
	public boolean contains(FunctionType ft) {
		return intersection(ft)==this;
	}

	/**
	 * Internal implementation of intersection with another FunctionType
	 * 
	 * Note we try very hard to return 'this' if possible without any allocation,
	 * in order to optimise the common case of equivalent function types, and to provide
	 * an efficient implementation for 'contains'
	 * @param ft
	 * @return
	 */
	protected Type intersection(FunctionType ft) {
		if (ft==this) return this;
		int thisN=getMinArity();
		int fN=ft.getMinArity();
		if (thisN<fN) {
			if (!isVariadic()) return Nothing.INSTANCE; // same arity not possible
			return ft.intersection(this); // ensure thisN>=fN 
		}
		if ((thisN>fN)&&(!ft.isVariadic())) {
			return Nothing.INSTANCE;
		}
		
		Type rt=getReturnType().intersection(ft.returnType);
		if (rt instanceof Nothing) return Nothing.INSTANCE;
		
		// compute intersection of fixed arity types
		Type[] ips=paramTypes;
		for (int i=0; i<thisN ; i++) {
			Type pt=paramTypes[i];
			Type it=pt.intersection(ft.getParamType(i));
			if (it!=pt) {
				if (ips==paramTypes) ips=paramTypes.clone(); // copy on first write
				ips[i]=it;
			}
		}
		Type vt=null;
		if (variadic&&ft.isVariadic()) {
			// both are variadic, so we need the intersected variadic type
			vt=variadicType.intersection(ft.getVariadicType());
		}
		
		if ((rt==returnType)&&(ips==paramTypes)&&(vt==variadicType)) return this;
		return create(rt,ips,vt);
	}
	
	/**
	 * Returns the type of a variadic parameters, or null if this function type is not variadic
	 * @return
	 */
	public Type getVariadicType() {
		return variadicType;
	}

	@Override
	public Type intersection(Type t) {
		if ((t==this)||(t instanceof Anything)) return this;
		
		if (t instanceof FunctionType) {
			return intersection((FunctionType)t);
		}
		if (t instanceof JavaType) {
			JavaType<?> jt = (JavaType<?>) t;
			if (jt.klass.isAssignableFrom(IFn.class)) {
				return this;
			} else if (IFn.class.isAssignableFrom(jt.klass)) {
				return jt;
			} else {
				return Nothing.INSTANCE;
			}
		}
		
		return t.intersection(this);
	}

	@Override
	public boolean canBeNull() {
		return false;
	}

	@Override
	public boolean canBeTruthy() {
		// a function is always a truthy value
		return true;
	}

	@Override
	public boolean canBeFalsey() {
		// a function is never a falsey value
		return false;
	}
	
	@Override
	public boolean cannotBeFalsey() {
		return true;
	}

	@Override
	public Type inverse() {
		return Not.createNew(this);
	}

	@Override
	public Type union(Type t) {
		if (t==this) return t;
		return Union.create(this,t);
	}
	
	@Override 
	public String toString() {
		StringBuilder sb=new StringBuilder ("(Fn [");
		for (Type t: paramTypes) {
			sb.append(t);
			sb.append(" ");
		}
		if (variadic) {
			sb.append("& ");
			// TODO: should this be a sequence type?
			sb.append(variadicType);
		}
		sb.append("] ");
		sb.append(returnType);
		sb.append(')');
		return sb.toString();
	}

	@Override
	public void validate() {
		if (minArity!=paramTypes.length) throw new TypeError("Mismatched arity count");
		if (variadic&&(variadicType==null)) throw new TypeError("Missing variadic type");
	}

	@Override
	public Type getParamType(int i) {
		if (variadic&&(i>=minArity)) return variadicType;
		return paramTypes[i];
	}

	@Override
	public boolean isVariadic() {
		return variadic;
	}

	@Override
	public int getMinArity() {
		return minArity;
	}

}
