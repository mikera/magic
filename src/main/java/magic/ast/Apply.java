package magic.ast;

import magic.Keywords;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.fn.IFn;
import magic.fn.IFn1;
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
	private Apply(APersistentList<Node<? extends Object>> form, APersistentMap<Keyword,Object> meta) {
		super(form,meta);
		this.function=(Node<IFn<? extends T>>)((Object)form.head());
		arity=form.size()-1;
		args=new Node<?>[arity];
		for (int i=0; i<arity; i++) {
			args[i]=form.get(i+1);
		}
	}
	
	@Override
	public Node<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Apply<T>(nodes,meta);
	}
	
	public static <T> Apply<T> create(APersistentList<Node<? extends Object>> form, SourceInfo sourceInfo) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE, sourceInfo);
		return create(form,meta);
	}
	
	private Apply<T> create(Node<IFn<? extends T>> newFunction, Node<?>[] newBody,  APersistentMap<Keyword, Object> meta) {
		APersistentList<Node<? extends Object>> form=Lists.cons(newFunction, PersistentList.wrap(newBody));
		return create(form,meta);
	}
	
	public static <T> Apply<T> create(APersistentList<Node<? extends Object>> form, APersistentMap<Keyword, Object> meta) {
		meta=meta.assoc(Keywords.DEPS, calcDependencies(form));
		return new Apply<T>(form,meta);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		EvalResult<?> rf= (EvalResult<?>) function.eval(c,bindings);
		if (rf.isEscaping()) return (EvalResult<T>) rf;
		
		Object rfo=rf.getValue();
		if (!(rfo instanceof IFn)) {
			throw new Error("Function expected in "+this+" but got "+rfo.getClass());
		}
		IFn<? extends T> f=(IFn<? extends T>) rfo;
		
		Object[] values=new Object[arity];
		EvalResult<?> r=rf;
		for (int i=0; i<arity; i++) {
			r=args[i].eval(c,bindings);
			if (r.isEscaping()) return (EvalResult<T>) r;
			values[i]=r.getValue();
		}
		return EvalResult.create(r.getContext(),f.applyToArray(values));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<IFn<? extends T>> newFunction=(Node<IFn<? extends T>>) fn.apply(function);
		boolean changed=false;
		Node<?>[] newBody=args;
		for (int i=0; i<arity; i++) {
			Node<?> node=args[i];
			Node<?> newNode=fn.apply(node);
			if (node!=newNode) {
				if (!changed) {
					newBody=newBody.clone();
					changed=true;
				}
				newBody[i]=newNode;
			} 
		}
		return (newFunction==function)&&(args==newBody)?this:create(newFunction,newBody,meta());	
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node<T> optimise() {	
		// optimise constant functions, i.e. function is known at compile time
		if (function.isConstant()) {
			IFn<? extends T> f=function.getValue();
			if (arity==0) return Constant.create(f.apply());

			// inlining specified at call site
			APersistentMap<Keyword,Object> meta=meta();
			if (meta.containsKey(Keywords.INLINE)&&f instanceof Lambda.LambdaFn) {
				Lambda<T>.LambdaFn lf=(Lambda<T>.LambdaFn) f;
				
				Let<? extends T> let=Let.create(lf.getParams().toArray(new Symbol[arity]), args, lf.getBody());
				return (Node<T>) let.optimise(); 
			}

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
