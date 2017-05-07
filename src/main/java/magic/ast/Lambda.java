package magic.ast;

import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.IPersistentVector;
import magic.data.Symbol;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.fn.IFn;
import magic.lang.Context;

public class Lambda<T> extends Node<IFn<T>> {

	private final IPersistentVector<Symbol> args;
	private final Node<T> body;
	private final int arity;

	public Lambda(IPersistentVector<Symbol> args, Node<T> body) {
		super(body.getDependencies().excludeAll(args));
		this.args=args;
		this.arity=args.size();
		this.body=body;
	}

	@Override
	public Result<IFn<T>> eval(Context context,APersistentMap<Symbol, Object> bindings) {
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
		return new Result<IFn<T>>(context,fn);
	}
	
	@Override
	public Node<IFn<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		bindings=bindings.delete(args); // hidden by argument bindings
		Node<T> newBody=body.specialiseValues(bindings);
		// System.out.println("Defining lambda as "+newBody+" with bindings "+bindings);
		return (body==newBody)?this:create(args,newBody);
	}

	public static <T> Lambda<T> create(IPersistentVector<Symbol> args, Node<T> body) {
		return new Lambda<T>(args,body);
	}
	
	@Override
	public String toString() {
		return "(Lambda "+args+" "+body+")";
	}

}
