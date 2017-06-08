package magic.ast;

import magic.Type;
import magic.compiler.AExpander;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * AST `let` node capable of defining lexical bindings
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Let<T> extends BaseForm<T> {
	private final int nLets;
	private final Node<T> body;
	private final Symbol[] syms;
	private final Node<? extends Object>[] lets;
	
	@SuppressWarnings("unchecked")
	public Let(Symbol[] syms, Node<? extends Object>[] lets, Node<T> bodyExpr,SourceInfo source) {
		super((APersistentList<Node<?>>)(APersistentList<?>)Lists.of(Constant.create(Symbols.LET),letVector(syms,lets),(Node<Object>)bodyExpr),bodyExpr.getDependencies().excludeAll(syms),source);
		nLets=syms.length;
		if (nLets!=lets.length) throw new IllegalArgumentException("Incorrect number of bindings forms for let");
		this.syms=syms;
		this.lets=lets;
		body=bodyExpr;
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
	public static <T> Let<T> create(Vector<? extends Object> bindings, APersistentList<Node<?>> body, SourceInfo si) {
		int nb=bindings.size();
		if ((nb&1)!=0) throw new Error("Let requires an even number of forms in binding vector");
		int n=nb/2;
		
		Symbol [] syms=new Symbol[n];
		Node<? extends Object> [] lets=new Node[n];
		for (int i=0; i<n; i++) {
			syms[i]=bindings.get(i*2).getSymbol();
			lets[i]=bindings.get(i*2+1);
		}
		Node<Object> bodyExpr=(body.size()==1)?(Node<Object>)body.get(0):Do.create(body,null);
		return (Let<T>) create(syms,lets,bodyExpr,si);
	}


	public static <T> Node<T> create(Node<? extends Object>[] body, SourceInfo source) {
		return Do.create(body,source);
	}
	
	public static <T> Node<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<?>[] bodyExprs) {
		return create(syms,lets,Do.create(bodyExprs));
	}
	
	public static <T> Let<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<?>[] bodyExprs,SourceInfo source) {
		return create(syms,lets,Do.create(bodyExprs),source);
	}
	
	public static <T> Let<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<T> body,SourceInfo source) {
		return new Let<T>(syms,lets,body,source);
	}
	
	public static <T> Let<T> create(Symbol[] syms,Node<? extends Object>[] lets,Node<T> bodyExpr) {
		return create(syms,lets,bodyExpr,null);
	}
	
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		for (int i=0; i<nLets; i++) {
			bindings=bindings.assoc(syms[i], (Object)(lets[i].compute(context,bindings)));
		}
		
		EvalResult<T> r=body.eval(context,bindings);
		return r;
	}
	
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<? extends Object>[] newLets=lets;
		boolean changed=false;
		for (int i=0; i<nLets; i++) {
			Node<? extends Object> node=lets[i];
			Node<? extends Object> newNode=node.specialiseValues(bindings);
			if (node!=newNode) {
				if (!changed) {
					newLets=newLets.clone();
					changed=true;
				}
				newLets[i]=newNode;
			} 
			if (newNode.isConstant()) {
				bindings=bindings.assoc(syms[i],newNode.getValue());
			} else {
		        bindings=bindings.dissoc(syms[i]); // binding no longer visible, must be calculated
			}
		}

		Node<? extends T> newBody=body.specialiseValues(bindings);
		return ((body==newBody)&&(lets==newLets))?this:create(syms,newLets,newBody);
	}
	
	@Override
	public Node<? extends T> optimise() {
		Let<T> newLet=mapChildren(NodeFunctions.optimise());
		return newLet.optimiseLocal();
	}
	
	private Node<? extends T> optimiseLocal() {
		if (lets.length==0) return body;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Let<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
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
		return ((body==newBody)&&(lets==newLets))?this:(Let<T>) create(syms,lets,newBody,getSourceInfo());
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
