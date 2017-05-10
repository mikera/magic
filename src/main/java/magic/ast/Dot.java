package magic.ast;

import java.lang.reflect.Method;

import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * Node representing a Java interop invocation
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Dot<T> extends Node<T> {

	// TODO: consider caching reflected methods?
	
	private final Node<?> instance;
	private final Symbol method;
	private final Node<?>[] args;
	private final int nArgs;

	private Dot(APersistentSet<Symbol> deps, Node<?> instance, Symbol method, Node<?>[] args) {
		super(deps);
		this.instance=instance;
		this.method=method;
		this.args=args;
		this.nArgs=args.length;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Dot<T> create(Node<?> instance, Symbol method, Node<?>[] args) {
		APersistentSet<Symbol> deps=instance.getDependencies();
		for (Node<?> a: args) {
			deps=deps.includeAll(a.getDependencies());
		}
		return (Dot<T>)new Dot<>(deps,instance, method,args);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		EvalResult<Object> r= (EvalResult<Object>) instance.eval(c, bindings);
		Object o=r.getValue();
		Class<?> klass=o.getClass();
		Object[] argVals=new Object[nArgs];
		Class<?>[] argClasses=new Class<?>[nArgs];
		for (int i=0; i<nArgs; i++) {
			r=(EvalResult<Object>) args[i].eval(c, bindings);
			Object arg=r.getValue();
			argVals[i]=arg;
			argClasses[i]=arg.getClass();
		}
		Method m;
		try {
			m = klass.getMethod(method.getName(), argClasses);
		} catch (Throwable e) {
			throw new Error("Unable to identify method "+method+" on object of class "+klass+" with argument classes "+argClasses,e);
		}
		
		try {
			return new EvalResult<T>(c,(T) m.invoke(o, argVals));
		} catch (Throwable t) {
			throw new Error("Reflected method invocation failed",t);
		}
	}

	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return this;
	}

	@Override
	public Node<T> optimise() {
		return this;
	}

	@Override
	public String toString() {
		return "(Dot ...)";
	}

}
