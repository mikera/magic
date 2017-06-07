package magic.ast;

import java.lang.reflect.Method;

import magic.Reflector;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Lists;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * Node representing a Java interop invocation, of the form:
 *    (. Classname methodName & args)
 * 
 * @author Mike
 *
 * @param <T>
 */
public class InvokeStatic<T> extends BaseForm<T> {

	// TODO: consider caching reflected methods?
	
	private final Class<?> klass;
	private final Symbol method;
	private final Node<?>[] args;
	private final int nArgs;

	@SuppressWarnings("unchecked")
	private InvokeStatic(APersistentSet<Symbol> deps, Class<?> klass, Symbol method, Node<?>[] args,SourceInfo source) {
		super(Lists.of(
				(Node<Symbol>)Constant.create(Symbols.DOT), 
				List.createCons(Constant.create(method),List.create(args),null)  
				)
				, deps,source);
		this.klass=klass;
		this.method=method;
		this.args=args;
		this.nArgs=args.length;
	}
	
	public static <T> InvokeStatic<T> create(Class<?> klass, Symbol method, Node<?>[] args,SourceInfo source) {
		APersistentSet<Symbol> deps=Sets.emptySet();
		for (Node<?> a: args) {
			deps=deps.includeAll(a.getDependencies());
		}
		return new InvokeStatic<T>(deps,klass, method,args,source);
	}
	
	public static <T> InvokeStatic<T> create(Class<?> klass, Symbol method, Node<? super Object>[] args) {
		return create(klass, method,args,null);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		Object[] argVals=new Object[nArgs];
		Class<?>[] argClasses=new Class<?>[nArgs];
		
		EvalResult<Object> r;
		for (int i=0; i<nArgs; i++) {
			r=(EvalResult<Object>) args[i].eval(c, bindings);
			Object arg=r.getValue();
			argVals[i]=arg;
			argClasses[i]=(arg==null)?Object.class:arg.getClass();
		}
		Method m = Reflector.getDeclaredMethod(klass,method.getName(), argClasses);
		
		try {
			Object result=m.invoke(null, argVals);
			return new EvalResult<T>(c,(T)result );
		} catch (Throwable t) {
			throw new Error("Reflected method invocation failed",t);
		}
	}

	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		// TODO
		return this;
	}

	@Override
	public Node<T> optimise() {
		// TODO
		return this;
	}

	@Override 
	public String toString() {
		StringBuilder sb=new StringBuilder ("(. ");
		sb.append(klass.getName());
		sb.append(" ");
		sb.append(method);
		for (Node<?> a : args) {
			sb.append(' ');
			sb.append(a);
		}
		sb.append(")");
		return sb.toString();
	}

}
