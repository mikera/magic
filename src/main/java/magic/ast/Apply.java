package magic.ast;

import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
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

	private final Node<IFn<T>> function;
	private final Node<?>[] args;
	private final int arity;

	@SuppressWarnings("unchecked")
	private Apply(APersistentList<Node<?>> form, SourceInfo source) {
		super(form,calcDependencies(form),source);
		this.function=(Node<IFn<T>>)form.head();
		arity=form.size()-1;
		args=new Node<?>[arity];
		for (int i=0; i<arity; i++) {
			args[i]=form.get(i+1);
		}
	}
	
	public static <T> Apply<T> create(APersistentList<Node<?>> form, SourceInfo sourceInfo) {
		return new Apply<T>(form,sourceInfo);
	}
	
	private Apply<T> create(Node<IFn<T>> newFunction, Node<?>[] newBody, SourceInfo source) {
		APersistentList<Node<?>> form=PersistentList.wrap(newBody).include(newFunction);
		return create(form,source);
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
