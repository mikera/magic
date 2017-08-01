package magic.ast;

import magic.Keywords;
import magic.Symbols;
import magic.Type;
import magic.Types;
import magic.compiler.AnalysisContext;
import magic.compiler.EvalResult;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.type.FunctionType;

/**
 * AST node representing a lambda expression a.k.a. "(fn [...] ...)"
 * 
 * Supports variadic args in the form (fn [a b & more] ...)
 * 
 * @author Mike
 *
 * @param <T> The return type of the lambda function
 */
public class Lambda<T> extends BaseForm<AFn<T>> {

	private final APersistentVector<Symbol> params;
	private final Node<T> body;
	private final int arity; // minimum arity (excludes trailing varargs)
	private final boolean variadic;
  
	@SuppressWarnings("unchecked")
	private Lambda(APersistentVector<Symbol> params, Node<T> body,boolean variadic,APersistentMap<Keyword, Object> meta) {
		super((APersistentList<Node<?>>)(APersistentList<?>)Lists.of(Lookup.create(Symbols.FN),Constant.create(params),body),meta);
		this.params=params;
		this.arity=params.size()-(variadic?2:0); // ignore ampersand and vararg parameter
		this.body=body;
		this.variadic=variadic;
	}

	@Override
	public Lambda<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Lambda<T>(params,body,variadic,meta);
	}
	
	public static <T> Lambda<T> create(APersistentVector<Symbol> params, Node<T> body) {
		return create(params,body,Maps.empty());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Lambda<T> create(APersistentVector<Symbol> params, Node<T> body,APersistentMap<Keyword,Object> meta) {
		int n=params.size();
		boolean variadic=false;
		if ((n>=2)&&(params.get(n-2)==Symbols.AMPERSAND)) {
			variadic=true;
		}
		APersistentSet<Symbol> deps=body.getDependencies().excludeAll(params);
		APersistentSet<Symbol> oldDeps=(APersistentSet<Symbol>) meta.get(Keywords.DEPS);
		if (oldDeps!=null) deps=deps.includeAll(oldDeps);
		meta=meta.assoc(Keywords.DEPS,deps);
		return new Lambda<T>(params,body,variadic,meta);
	}

	@SuppressWarnings("unchecked")
	public static <T> Lambda<T> create(Vector<Symbol> params, APersistentList<Node<?>> body, APersistentMap<Keyword,Object> meta) {
		APersistentVector<Symbol> alist=(APersistentVector<Symbol>) params.toForm();
		Node<T> bodyExpr=(body.size()==1)?(Node<T>) body.get(0):Do.create(body);
		return create(alist,bodyExpr,meta);
	}

	/**
	 * Computes the function object represented by this lambda. 
	 * 
	 * Specialises the body expression according to the provided context and bindings
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<AFn<T>> eval(Context context,APersistentMap<Symbol, Object> bindings) {
		APersistentSet<Symbol> deps=getDependencies(); // free deps
		APersistentMap<Symbol, Object> depVals=(APersistentMap<Symbol, Object>) Maps.EMPTY;
		for (Symbol dep: deps) {
			if (bindings.containsKey(dep)) {
				depVals=depVals.assoc(dep, bindings.get(dep));
			} else {
				depVals=depVals.assoc(dep, context.getValue(dep));
			}
		}
		
		final APersistentMap<Symbol, Object> capturedBindings=depVals;
		// capture variables defined in the current scope
		Node<? extends T> body=this.body.specialiseValues(capturedBindings);
		
		// System.out.println(body);
		AFn<T> fn=new LambdaFn(body,capturedBindings);
		return new EvalResult<AFn<T>>(context,fn);
	}
	
	public final class LambdaFn extends AFn<T> {
		private static final long serialVersionUID = 4368281324742419123L;
		
		private final APersistentMap<Symbol, Object> capturedBindings;
		private Node<? extends T> body;

		private LambdaFn(Node<? extends T> body,APersistentMap<Symbol, Object> capturedBindings) {
			this.capturedBindings = capturedBindings;
			this.body=body;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T applyToArray(Object... a) {
			int alength=a.length;
			if (variadic) {
				if (alength<arity) throw new ArityException(arity,a.length);
			} else {
				if (alength!=arity) throw new ArityException(arity,a.length);	
			}
			// Context c=context;
			APersistentMap<Symbol, Object> bnds=capturedBindings;
			// add function arguments to the lexical bindings
			for (int i=0; i<arity; i++) {
				Symbol param=params.get(i);
				if (param==Symbols.UNDERSCORE) continue; // ignore bindings on underscore
				bnds=bnds.assoc(param, a[i]);
			}
			if (variadic) {
				Symbol varParam=params.get(arity+1); // symbol after ampersand
				if (varParam!=Symbols.UNDERSCORE) {
					Tuple<?> vs=Tuple.wrap(a, arity, alength-arity); // construct arg tuple
					bnds=bnds.assoc(varParam, vs);
				}
			}
			EvalResult<T> r=(EvalResult<T>) body.eval(null,bnds); // TODO: shouldn't do any context lookup?
			return r.getValue(); 
		}

		@Override
		public Type getReturnType() {
			return body.getType();
		}

		@Override
		public String toString() {
			return super.toString()+":"+Lambda.this.toString();
		}

		@Override
		public boolean hasArity(int i) {
			return variadic?(i>=arity):(i==arity);
		}

		public APersistentVector<Symbol> getParams() {
			return params;
		}

		public Node<? extends T> getBody() {
			return body;
		}

		@Override
		public int arity() {
			return arity;
		}

		@Override
		public Type getType() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Lambda<T> analyse(AnalysisContext context) {
		for (int i=0; i<arity; i++) {
			context=context.bind(params.get(i), Constant.create(null));
		}
		if (variadic) {
			// skip the & character, get the last symbol
			context=context.bind(params.get(arity+1), Constant.create(null));
		}
		Node<? extends T> newBody=(Node<? extends T>) body.analyse(context);
		
		return (body==newBody)?this:(Lambda<T>) create(params,newBody,meta());
	}
	
	@Override
	public Node<? extends AFn<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		bindings=bindings.delete(params); // hidden by argument bindings
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	
	@Override
	public Node<? extends AFn<T>> optimise() {
		Lambda<T> lambda=mapChildren(NodeFunctions.optimise());
		return lambda;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Lambda<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<? extends T> newBody=(Node<? extends T>) fn.apply(body);
		return (body==newBody)?this:(Lambda<T>) create(params,newBody,meta());
	}
	
	/**
	 * Returns the type of this `lambda` expression, i.e. the a function type returning the type of the body
	 */
	@Override
	public Type getType() {
		int n=arity;
		Type[] argTypes=new Type[n];
		for (int i=0; i<n; i++) {
			// TODO: specialised argument types
			argTypes[i]=Types.ANY;
		}
		return FunctionType.create(body.getType(),argTypes);
	}
}
