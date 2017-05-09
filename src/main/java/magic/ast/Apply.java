package magic.ast;

import magic.compiler.EvalResult;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.fn.IFn;
import magic.lang.Context;

/**
 * AST node representing a function application
 * 
 * @author Mike
 */
public class Apply<T> extends Node<T> {

	private Node<IFn<T>> function;
	private Node<?>[] args;
	private int arity;

	public Apply(Node<IFn<T>> f, Node<?>... args) {
		super(calcDependencies(f,args));
		this.function=f;
		this.args=args;
		arity=args.length;
	}

	@Override
	public EvalResult<T> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		EvalResult<IFn<T>> rf= function.eval(c,bindings);
		IFn<T> f=rf.getValue();
		Object[] values=new Object[arity];
		EvalResult<?> r=rf;
		for (int i=0; i<arity; i++) {
			r=args[i].eval(c,bindings);
			values[i]=r.getValue();
		}
		return EvalResult.create(r.getContext(),f.applyToArray(values));
	}

	public static <T> Apply<T> create(Node<IFn<T>> function, Node<?>... args) {
		return new Apply<T>(function,args);
	}

	public static <T> Apply<T> create(Node<IFn<T>> function, APersistentList<Node<?>> tail) {
		return create(function,tail.toArray(new Node[tail.size()]));
	}
	

	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<IFn<T>> newFunction=function.specialiseValues(bindings);
		boolean changed=false;
		Node<?>[] newBody=args;
		for (int i=0; i<arity; i++) {
			Node<?> node=args[i];
			Node<?> newNode=node.specialiseValues(bindings);
			if (node!=newNode) {
				if (!changed) {
					newBody=newBody.clone();
					changed=true;
				}
				newBody[i]=newNode;
			} 
		}
		return (newFunction==function)&&(args==newBody)?this:create(newFunction,newBody);	
	}

	@Override
	public Node<T> optimise() {
		if ((arity==0)&&(function.isConstant())) {
			return Constant.create(function.getValue().apply());
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("(Apply ");
		sb.append(function);
		for (int i=0; i<arity; i++) {
			sb.append(' ');
			sb.append(args[i]);
		}
		sb.append(')');
		return sb.toString();
	}
}
