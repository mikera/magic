package magic;

import java.lang.reflect.Method;

import magic.data.APersistentVector;
import magic.data.Tuple;

public class Reflector {

	public static Method getDeclaredMethod(Class<?> klass, String method, Class<?>[] argClasses) {
		try {
			return klass.getDeclaredMethod(method, argClasses);
		} catch (NoSuchMethodException m) {
			// OK, we need to seach...
		};
		int arity=argClasses.length;
		APersistentVector<Method> matches=getMatchingMethods(klass,method,arity);
		if (matches.size()==0) {
			throw new Error("Unable to identify any static methods called '"+method+"' on class "+klass.getName()+" with arity "+arity);
		}
		
		for (Method m: matches) {
			Class<?>[] paramTypes=m.getParameterTypes();
			if (isAssignable(paramTypes,argClasses)) return m;
		}
		
		throw new Error("Unable to identify static method '"+method+"' on class "+klass.getName()+" with argument classes ["+RT.toString(argClasses, ",")+"]");

	}

	private static boolean isAssignable(Class<?>[] paramTypes, Class<?>[] argClasses) {
		int n=paramTypes.length;
		for (int i=0; i<n; i++) {
			if (!paramTypes[i].isAssignableFrom(argClasses[i])) return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static APersistentVector<Method> getMatchingMethods(Class<?> klass, String name, int arity) {
		APersistentVector<Method> methods=(APersistentVector<Method>) Tuple.EMPTY;
		
		Method[] allMethods=getAllMethods(klass);
		for (Method m: allMethods) {
			if (!m.getName().equals(name)) continue;
			Class<?>[] paramTypes=m.getParameterTypes();
			if (paramTypes.length!=arity) continue;
			methods=methods.include(m);
		}
		
		return methods;
	}

	private static Method[] getAllMethods(Class<?> klass) {
		// TODO: cache this!
		return klass.getMethods();
	}
} 
