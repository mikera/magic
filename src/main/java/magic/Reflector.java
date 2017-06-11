package magic;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import magic.data.APersistentVector;
import magic.data.Tuple;

public class Reflector {

	/**
	 * Gets a declared static methods for a given class
	 * @param klass
	 * @param method
	 * @param argClasses
	 * @return
	 */
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
	
	/**
	 * Gets a method for a given class
	 * @param klass
	 * @param method
	 * @param argClasses
	 * @return
	 */	
	public static Method getMethod(Object instance, String method, Class<?>[] argClasses) {
		Class<?> klass=instance.getClass();
		Method m;
		try {
			m = klass.getMethod(method, argClasses);
		} catch (Throwable e) {
			throw new Error("Unable to identify method '"+method+"' in object of class '"+klass.getName()+"' with argument classes: ["+RT.toString(argClasses, ",")+"]",e);
		}
		return m;
	}
	
	/**
	 * Gets a method handle
	 * @param klass
	 * @param method
	 * @param argClasses
	 * @return
	 */	
	public static MethodHandle getMethodHandle(Object instance, String methodName, Class<?>[] argClasses) {
		Class<?> klass=instance.getClass();
		Method m=getMethod(instance,methodName,argClasses);
		MethodType mt=MethodType.methodType(m.getReturnType(),m.getParameterTypes());
		try {
			return MethodHandles.lookup().findVirtual(klass,methodName,mt);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new magic.Error("Unable to get method handle",e);
		}
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

	public static MethodHandle getStaticMethodHandle(Class<?> klass, String name, Class<?>[] argClasses) {
		int arity = argClasses.length;
		// get candidate method matches
		APersistentVector<Method> methods=getMatchingMethods(klass,name,arity);
		if (methods.size()==0) return null;
		
		Method m=methods.get(0);
		MethodType mt=MethodType.methodType(m.getReturnType(),m.getParameterTypes());
		try {
			return MethodHandles.lookup().findStatic(klass,name,mt);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new magic.Error("Unable to get static method handle",e);
		}
	}
} 
