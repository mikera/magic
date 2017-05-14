package magic.ast;

import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Symbol;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn;
import magic.lang.Context;
import magic.type.FunctionType;

/**
 * AST node representing a lambda expression a.k.a. "(fn [...] ...)"
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Lambda<T> extends Node<IFn<T>> {

	private final APersistentVector<Symbol> args;
	private final Node<T> body;
	private final int arity;
  
	public Lambda(APersistentVector<Symbol> args, Node<T> body,SourceInfo source) {
		super(body.getDependencies().excludeAll(args),source);
		this.args=args;
		this.arity=args.size();
		this.body=body;
	}
	

	public static <T> Lambda<T> create(APersistentVector<Symbol> args, Node<T> body) {
		return create(args,body,null);
	}
	
	public static <T> Lambda<T> create(APersistentVector<Symbol> args, Node<T> body,SourceInfo source) {
		return new Lambda<T>(args,body,source);
	}

	@Override
	public EvalResult<IFn<T>> eval(Context context,APersistentMap<Symbol, Object> bindings) {
		Node<T> body=this.body.specialiseValues(bindings.delete(args));
		// System.out.println(body);
		AFn<T> fn=new AFn<T>() {
			@Override
			public T applyToArray(Object... a) {
				if (a.length!=arity) throw new ArityException(arity,a.length);
				Context c=context;
				APersistentMap<Symbol, Object> bnds=bindings;
				for (int i=0; i<arity; i++) {
					bnds=bnds.assoc(args.get(i), a[i]);
				}
				return body.compute(c,bnds);
			}	
		};
		return new EvalResult<IFn<T>>(context,fn);
	}
	
	@Override
	public Node<IFn<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		bindings=bindings.delete(args); // hidden by argument bindings
		Node<T> newBody=body.specialiseValues(bindings);
		// System.out.println("Defining lambda as "+newBody+" with bindings "+bindings);
		return (body==newBody)?this:create(args,newBody);
	}
	
	@Override
	public Node<IFn<T>> optimise() {
		Node<T> newBody=body.optimise();
		return (body==newBody)?this:create(args,newBody);
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
		return "(Lambda "+args+" "+body+")";
	}
}
