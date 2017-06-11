package magic.ast;

import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Symbols;
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
	private Lambda(APersistentVector<Symbol> params, Node<T> body,SourceInfo source, boolean variadic) {
		super((APersistentList<Node<?>>)(APersistentList<?>)Lists.of(Constant.create(Symbols.FN),Constant.create(params),body),body.getDependencies().excludeAll(params),source);
		this.params=params;
		this.arity=params.size()-(variadic?2:0); // ignore ampersand and vararg parameter
		this.body=body;
		this.variadic=variadic;
	}
	
	public static <T> Lambda<T> create(APersistentVector<Symbol> params, Node<T> body) {
		return create(params,body,null);
	}
	
	public static <T> Lambda<T> create(APersistentVector<Symbol> params, Node<T> body,SourceInfo source) {
		int n=params.size();
		boolean variadic=false;
		if ((n>=2)&&(params.get(n-2)==Symbols.AMPERSAND)) {
			variadic=true;
		}
		
		return new Lambda<T>(params,body,source,variadic);
	}

	@SuppressWarnings("unchecked")
	public static <T> Lambda<T> create(Vector<Symbol> params, APersistentList<Node<?>> body, SourceInfo source) {
		APersistentVector<Symbol> alist=(APersistentVector<Symbol>) params.toForm();
		Node<T> bodyExpr=(body.size()==1)?(Node<T>) body.get(0):Do.create(body,source);
		return create(alist,bodyExpr,source);
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
	
	private final class LambdaFn extends AFn<T> {
		private static final long serialVersionUID = 4368281324742419123L;
		
		private final APersistentMap<Symbol, Object> capturedBindings;
		private Node<? extends T> body;

		private LambdaFn(Node<? extends T> body,APersistentMap<Symbol, Object> capturedBindings) {
			this.capturedBindings = capturedBindings;
			this.body=body;
		}

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
				Tuple<?> vs=Tuple.wrap(a, arity, alength-arity); // construct arg tuple
				bnds=bnds.assoc(varParam, vs);
			}
			return body.compute(null,bnds); // shouldn't do any context lookup?
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
	}
	
	@Override
	public Node<? extends AFn<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		bindings=bindings.delete(params); // hidden by argument bindings
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	
	@Override
	public Node<? extends AFn<T>> optimise() {
		return mapChildren(NodeFunctions.optimise());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends AFn<T>> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<? extends T> newBody=(Node<? extends T>) fn.apply(body);
		return (body==newBody)?this:(Lambda<T>) create(params,newBody,getSourceInfo());
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
			argTypes[i]=Types.ANYTHING;
		}
		return FunctionType.create(body.getType(),argTypes);
	}
	
	@Override
	public String toString() {
		return "(fn "+params+" "+body+")";
	}

}
