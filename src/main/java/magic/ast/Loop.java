package magic.ast;

import magic.Keywords;
import magic.Symbols;
import magic.Type;
import magic.compiler.AExpander;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.fn.ArityException;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST `loop` node capable of defining lexical bindings and looping via `recur`
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Loop<T> extends BaseForm<T> {
	private final int nLets;
	private final Node<T> body;
	private final Symbol[] syms;
	private final Node<? extends Object>[] lets;
	
	@SuppressWarnings("unchecked")
	private Loop(Symbol[] syms, Node<? extends Object>[] lets, Node<T> bodyExpr,APersistentMap<Keyword, Object> meta) {
		super((APersistentList<Node<?>>)(APersistentList<?>)Lists.of(Constant.create(Symbols.LOOP),letVector(syms,lets),(Node<Object>)bodyExpr),meta);
		nLets=syms.length;
		this.syms=syms;
		this.lets=lets;
		body=bodyExpr;
	}
	
	@Override
	public Node<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Loop<T>(syms,lets,body,meta);
	}

	@SuppressWarnings("unchecked")
	private static Vector<Object> letVector(Symbol[] syms2, Node<? extends Object>[] lets2) {
		int n=syms2.length;
		if (lets2.length!=n) throw new Error("Wrong number of lets!");
		Node<? extends Object>[] vs=new Node[n*2];
		for (int i=0; i<n; i++) {
			vs[i*2]=Lookup.create(syms2[i]);
			vs[i*2+1]=lets2[i];
		}
		return Vector.create(vs);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Loop<T> create(Vector<? extends Object> bindings, APersistentList<Node<?>> body, SourceInfo si) {
		int nb=bindings.size();
		if ((nb&1)!=0) throw new Error("Loop requires an even number of forms in binding vector");
		int n=nb/2;
		
		Symbol [] syms=new Symbol[n];
		Node<? extends Object> [] lets=new Node[n];
		for (int i=0; i<n; i++) {
			syms[i]=bindings.get(i*2).getSymbol();
			lets[i]=bindings.get(i*2+1);
		}
		Node<Object> bodyExpr=(body.size()==1)?(Node<Object>)body.get(0):Do.create(body);
		return (Loop<T>) create(syms,lets,bodyExpr,si);
	}
	
	public static <T> Node<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<?>[] bodyExprs) {
		return create(syms,lets,Do.create(bodyExprs));
	}
	
	public static <T> Loop<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<?>[] bodyExprs,SourceInfo source) {
		return create(syms,lets,Do.create(bodyExprs),source);
	}
	
	public static <T> Loop<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<T> body,SourceInfo source) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE,source);
		APersistentSet<Symbol> deps=body.getDependencies();
		int n=lets.length;
		if (n!=syms.length) throw new IllegalArgumentException("Incorrect number of bindings forms for loop");

		for (int i=n-1; i>=0; i--) {
			deps=deps.exclude(syms[i]); // let-bound symbol is provided to subsequent lets / body
			deps=deps.includeAll(lets[i].getDependencies());
		}
		meta=meta.assoc(Keywords.DEPS,deps);
				
		return new Loop<T>(syms,lets,body,meta);
	}
	
	public static <T> Loop<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<T> bodyExpr) {
		return create(syms,lets,bodyExpr,null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		for (int i=0; i<nLets; i++) {
			EvalResult<?> r=lets[i].eval(context,bindings);
			if (r.isEscaping()) {
				return (EvalResult<T>) r;
			}
			
			bindings=bindings.assoc(syms[i], r.getValue());
		}
		
		EvalResult<T> r=body.eval(context,bindings);
		
		while (r.isRecurring()) {
			Object[] rvs=(Object[]) r.getValue();
			if (rvs.length!=nLets) throw new ArityException("loop expects "+nLets+" arguments for recur but got: "+rvs.length );
			for (int i=0; i<nLets; i++) {
				bindings=bindings.assoc(syms[i], rvs[i]);
			}
			r=body.eval(context, bindings);
		}
		return r;
	}
	
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<? extends Object>[] newLets=lets;
		for (int i=0; i<nLets; i++) {
			Node<? extends Object> node=lets[i];
			Node<? extends Object> newNode=node.specialiseValues(bindings);
			if (node!=newNode) {
				if (lets==newLets) {
					newLets=lets.clone();
				}
				newLets[i]=newNode;
			} 
			
			if (newNode.isConstant()) {
				// if constant, we can optimise subsequent initialisations
				bindings=bindings.assoc(syms[i],newNode.getValue());
			} else {
		        bindings=bindings.dissoc(syms[i]); // must be calculated
			}
		}
		
		// don't specialise on any bound variables, may be recur targets within body
		for (int i=0; i<nLets; i++) {
	        bindings=bindings.dissoc(syms[i]); 
		}
		
		Node<? extends T> newBody=body.specialiseValues(bindings);
		return ((body==newBody)&&(lets==newLets))?this:create(syms,newLets,newBody);
	}
	
	@Override
	public Node<? extends T> optimise() {
		Loop<T> newLet=mapChildren(NodeFunctions.optimise());
		return newLet.optimiseLocal();
	}
	
	private Node<? extends T> optimiseLocal() {
		if (lets.length==0) return body;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Loop<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<? extends Object>[] newLets=lets;
		boolean changed=false;
		for (int i=0; i<nLets; i++) {
			Node<? extends Object> node=lets[i];
			Node<? extends Object> newNode=fn.apply(node);
			if (node!=newNode) {
				if (!changed) {
					newLets=newLets.clone();
					changed=true;
				}
				newLets[i]=newNode;
			} 
		}
		
		Node<? extends AExpander> newBody=(Node<? extends AExpander>) fn.apply(body);
		return ((body==newBody)&&(lets==newLets))?this:(Loop<T>) create(syms,lets,newBody,getSourceInfo());
	}
	
	/**
	 * Returns the type of this `do` expression, i.e. the type of the last subexpression
	 */
	@Override
	public Type getType() {
		return body.getType();
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("(let [");
		for (int i=0; i<nLets; i++) {
			if (i>0) sb.append(' ');
			sb.append(syms[i]);
			sb.append(' ');
			sb.append(lets[i]);
		}
		sb.append("] ");
		sb.append(body);
		sb.append(')');
		return sb.toString();
	}




}
