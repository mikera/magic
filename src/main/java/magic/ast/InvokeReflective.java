package magic.ast;

import java.lang.invoke.MethodHandle;

import magic.Reflector;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Keywords;
import magic.lang.Symbols;

/**
 * Node representing a Java reflective interop invocation, of the form:
 *    (. object methodName & args)
 * 
 * @author Mike
 *
 * @param <T>
 */
public class InvokeReflective<T> extends BaseForm<T> {

	// TODO: consider caching reflected methods?
	
	private final Node<?> instance;
	private final Symbol method;
	private final Node<?>[] args;
	private final int nArgs;

	@SuppressWarnings("unchecked")
	private InvokeReflective(Node<?> instance, Symbol method, Node<?>[] args,APersistentMap<Keyword,Object> meta) {
		super(Lists.of(
				(Node<Symbol>)Constant.create(Symbols.DOT), instance, 
				ListForm.createCons(Constant.create(method),ListForm.create(args),null)  
				)
				, meta);
		this.instance=instance;
		this.method=method;
		this.args=args;
		this.nArgs=args.length;
	}
	
	@Override
	public Node<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new InvokeReflective<T>(instance,method,args,meta);
	}
	
	public static <T> InvokeReflective<T> create(Node<?> instance, Symbol method, Node<?>[] args,SourceInfo source) {
		APersistentSet<Symbol> deps=instance.getDependencies();
		for (Node<?> a: args) {
			deps=deps.includeAll(a.getDependencies());
		}
		APersistentMap<Keyword,Object> meta=Maps.create(Keywords.SOURCE,source);
		meta=meta.assoc(Keywords.DEPS,deps);
		return new InvokeReflective<T>(instance, method,args,meta);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> InvokeReflective<T> create(Node<?> instance, Symbol method, Node<? super Object>[] args) {
		return create((Node<Object>)instance, method,args,null);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		EvalResult<Object> r= (EvalResult<Object>) instance.eval(c, bindings);
		Object o=r.getValue();
		
		Object[] argVals=new Object[nArgs+1]; // Includes instance, i.e. [o, args....]
		argVals[0]=o;
		Class<?>[] argClasses=new Class<?>[nArgs];
		for (int i=0; i<nArgs; i++) {
			r=(EvalResult<Object>) args[i].eval(c, bindings);
			Object arg=r.getValue();
			argVals[i+1]=arg;
			argClasses[i]=arg.getClass();
		}
		
		MethodHandle mh=Reflector.getMethodHandle(o,method.getName(), argClasses);
	
		try {
			// note mh.invoke(...) doesn't work because it is a special function that uses varargs only
			Object result=mh.invokeWithArguments(argVals);
			return new EvalResult<T>(c,(T) result);
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
		sb.append(instance);
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
