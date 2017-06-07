package magic.ast;

import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.lang.Context;
import magic.lang.Symbols;
import magic.type.FunctionType;

/**
 * AST node representing a lambda expression a.k.a. "(fn [...] ...)"
 * 
 * @author Mike
 *
 * @param <T> The return type of the lambda function
 */
public class Lambda<T> extends BaseForm<AFn<T>> {

	private final APersistentVector<Symbol> params;
	private final Node<T> body;
	private final int arity;
  
	@SuppressWarnings("unchecked")
	private Lambda(APersistentVector<Symbol> params, Node<T> body,SourceInfo source) {
		super((APersistentList<Node<?>>)(APersistentList<?>)Lists.of(Constant.create(Symbols.FN),Constant.create(params),body),body.getDependencies().excludeAll(params),source);
		this.params=params;
		this.arity=params.size();
		this.body=body;
	}
	
	
	public static <T> Lambda<T> create(APersistentVector<Symbol> params, Node<T> body) {
		return create(params,body,null);
	}
	
	public static <T> Lambda<T> create(APersistentVector<Symbol> params, Node<T> body,SourceInfo source) {
		return new Lambda<T>(params,body,source);
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
	@Override
	public EvalResult<AFn<T>> eval(Context context,APersistentMap<Symbol, Object> bindings) {
		final APersistentMap<Symbol, Object> capturedBindings=bindings.delete(params);
		// capture variables defined in the current scope
		Node<? extends T> body=this.body.specialiseValues(capturedBindings);
		
		// System.out.println(body);
		AFn<T> fn=new AFn<T>() {
			@Override
			public T applyToArray(Object... a) {
				if (a.length!=arity) throw new ArityException(arity,a.length);
				Context c=context;
				APersistentMap<Symbol, Object> bnds=capturedBindings;
				// add function arguments to the lexical bindings
				for (int i=0; i<arity; i++) {
					Symbol param=params.get(i);
					bnds=bnds.assoc(param, a[i]);
				}
				return body.compute(c,bnds);
			}	
			
			@Override
			public Type getReturnType() {
				return body.getType();
			}
			
			@Override
			public String toString() {
				return super.toString()+":"+Lambda.this.toString();
			}
		};
		return new EvalResult<AFn<T>>(context,fn);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends AFn<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		bindings=bindings.delete(params); // hidden by argument bindings
		Node<? extends T> newBody=body.specialiseValues(bindings);
		// System.out.println("Defining lambda as "+newBody+" with bindings "+bindings);
		return (body==newBody)?this:(Lambda<T>) create(params,newBody,getSourceInfo());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends AFn<T>> optimise() {
		Node<? extends T> newBody=body.optimise();
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
