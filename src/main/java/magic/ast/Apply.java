package magic.ast;

import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.fn.IFn;
import magic.lang.Context;

/**
 * AST node representing a function application
 * 
 * @author Mike
 */
public class Apply<T> extends BaseForm<T> {

	private final Node<IFn<? extends T>> function;
	private final Node<?>[] args;
	private final int arity;

	@SuppressWarnings("unchecked")
	private Apply(APersistentList<Node<? extends Object>> form, SourceInfo source) {
		super(form,calcDependencies(form),source);
		this.function=(Node<IFn<? extends T>>)((Object)form.head());
		arity=form.size()-1;
		args=new Node<?>[arity];
		for (int i=0; i<arity; i++) {
			args[i]=form.get(i+1);
		}
	}
	
	public static <T> Apply<T> create(APersistentList<Node<? extends Object>> form, SourceInfo sourceInfo) {
		return new Apply<T>(form,sourceInfo);
	}
	
	private Apply<T> create(Node<IFn<? extends T>> newFunction, Node<?>[] newBody, SourceInfo source) {
		APersistentList<Node<? extends Object>> form=Lists.cons(newFunction, PersistentList.wrap(newBody));
		return create(form,source);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		EvalResult<?> rf= (EvalResult<?>) function.eval(c,bindings);
		Object rfo=rf.getValue();
		if (!(rfo instanceof IFn)) {
			throw new Error("Function expected in "+this+" but got "+rfo.getClass());
		}
		IFn<? extends T> f=(IFn<? extends T>) rfo;
		Object[] values=new Object[arity];
		EvalResult<?> r=rf;
		for (int i=0; i<arity; i++) {
			r=args[i].eval(c,bindings);
			values[i]=r.getValue();
		}
		return EvalResult.create(r.getContext(),f.applyToArray(values));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<IFn<? extends T>> newFunction=(Node<IFn<? extends T>>) function.specialiseValues(bindings);
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
		return (newFunction==function)&&(args==newBody)?this:create(newFunction,newBody,source);	
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
		StringBuilder sb= new StringBuilder("(apply ");
		sb.append(function);
		for (int i=0; i<arity; i++) {
			sb.append(' ');
			sb.append(args[i]);
		}
		sb.append(')');
		return sb.toString();
	}


}
