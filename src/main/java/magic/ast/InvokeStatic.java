package magic.ast;

import java.lang.invoke.MethodHandle;

import magic.Keywords;
import magic.RT;
import magic.Reflector;
import magic.Symbols;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Sets;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * Node representing a Java static interop invocation, of the form:
 *    (. Classname methodName & args)
 *    
 * Requires that the method be fully resolved at construction time.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class InvokeStatic<T> extends BaseForm<T> {

	private MethodHandle method;	
	private final Node<?>[] args;
	private final int nArgs;

	@SuppressWarnings("unchecked")
	private InvokeStatic(MethodHandle method, Node<?>[] args, APersistentMap<Keyword,Object> meta) {
		super(Lists.of(
				(Node<Symbol>)Constant.create(Symbols.DOT), 
				ListForm.createCons(Constant.create(method),ListForm.create(args),null)  
				)
				, meta);
		this.method=method;
		this.args=args;
		this.nArgs=args.length;
	}
	
	@Override
	public Node<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new InvokeStatic<T>(method,args,meta);
	}
	
	public static <T> InvokeStatic<T> create(Class<?> klass, Symbol method, Node<?>[] args,SourceInfo source) {
		APersistentSet<Symbol> deps=Sets.emptySet();
		int n=args.length;
		Class<?>[] argClasses=new Class<?>[n];
		for (int i=0; i<n; i++) {
			Node<?> a=args[i];
			deps=deps.includeAll(a.getDependencies());
			argClasses[i]=a.getType().getJavaClass();
		}
		MethodHandle m = Reflector.getStaticMethodHandle(klass,method.getName(), argClasses);
		return create(deps,m,args,source);
	}
	
	private static <T> InvokeStatic<T> create(APersistentSet<Symbol> deps, MethodHandle m, Node<?>[] args, SourceInfo source) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.DEPS,deps);
		meta=meta.assoc(Keywords.SOURCE, source);
		return new InvokeStatic<T>(m,args,meta);
	}

	public static <T> InvokeStatic<T> create(Class<?> klass, Symbol method, Node<? super Object>[] args) {
		return create(klass, method,args,null);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c, APersistentMap<Symbol, Object> bindings) {
		Object[] argVals=new Object[nArgs];
		
		EvalResult<Object> r;
		for (int i=0; i<nArgs; i++) {
			r=(EvalResult<Object>) args[i].eval(c, bindings);
			Object arg=r.getValue();
			argVals[i]=arg;
		}
		
		try {
			Object result=method.invoke(null, argVals);
			return new EvalResult<T>(c,(T)result );
		} catch (Throwable t) {
			throw new magic.Error("Reflected method invocation failed.",t);
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
		return create(getDependencies(),method,newNodes,getSourceInfo());
	}
	
	@Override
	public String toString() {
		return "(INVOKESTATIC "+method+" "+RT.toString(args, " ")+")";
	}

}
