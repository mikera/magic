package magic.ast;

import java.lang.reflect.Method;

import magic.Keywords;
import magic.RT;
import magic.Reflector;
import magic.Symbols;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Sets;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * Node representing a Java reflective interop invocation, of the form:
 *    (. Classname methodName & args)
 * 
 * @author Mike
 *
 * @param <T>
 */
public class InvokeStaticReflective<T> extends BaseForm<T> {

	// TODO: consider caching reflected methods?
	
	private final Class<?> klass;
	private final Symbol method;
	private final Node<?>[] args;
	private final int nArgs;

	@SuppressWarnings("unchecked")
	private InvokeStaticReflective(Class<?> klass, Symbol method, Node<?>[] args,APersistentMap<Keyword, Object> meta) {
		super(Lists.of(
				(Node<Symbol>)Constant.create(Symbols.DOT), 
				ListForm.createCons(Constant.create(method),ListForm.create(args),null)  
				)
				, meta);
		this.klass=klass;
		this.method=method;
		this.args=args;
		this.nArgs=args.length;
	}
	
	@Override
	public InvokeStaticReflective<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new InvokeStaticReflective<T>(klass,method,args,meta);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> InvokeStaticReflective<T> create(Class<?> klass, Symbol method, Node<?>[] args,APersistentMap<Keyword, Object> meta) {
		APersistentSet<Symbol> deps=(APersistentSet<Symbol>) meta.get(Keywords.DEPS);
		if (deps==null) deps=Sets.emptySet();
		for (Node<?> a: args) {
			deps=deps.includeAll(a.getDependencies());
		}
		meta=meta.assoc(Keywords.DEPS, deps);
		return new InvokeStaticReflective<T>(klass, method,args,meta);
	}
	
	public static <T> InvokeStaticReflective<T> create(Class<?> klass, Symbol method, Node<? super Object>[] args) {
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
		String methodName=method.getName();
		Method m = Reflector.getDeclaredMethod(klass,methodName, argClasses);
		if (m==null) {
			throw new Error ("Method "+methodName+" not found on class"+klass+" with argument types "+RT.arrayToString(argClasses," "));
		}
		
		try {
			Object result=m.invoke(null, argVals);
			return new EvalResult<T>(c,(T)result );
		} catch (Throwable t) {
			throw new Error("Reflected method invocation failed on "+klass+"/"+methodName+" with arguments "+RT.arrayToString(argVals," "),t);
		}
	}

	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}

	@Override
	public Node<? extends T> optimise() {
		return mapChildren(NodeFunctions.optimise());
	}
	
	@Override
	public Node<? extends T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<?>[] newNodes=NodeFunctions.mapAll(args,fn);
		if (newNodes==args) return this;
		return create(klass,method,newNodes,meta());
	}

	@Override 
	public String toString() {
		StringBuilder sb=new StringBuilder ("(INVOKE-STATIC-REFLECTIVE ");
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
