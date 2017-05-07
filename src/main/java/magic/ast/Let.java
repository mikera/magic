package magic.ast;

import magic.RT;
import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * AST `let` node capable of defining lexical bindings
 * @author Mike
 *
 * @param <T>
 */
public class Let<T> extends Node<T> {
	private final int nLets;
	private final Node<T> body;
	private final Symbol[] syms;
	private final Node<?>[] lets;
	
	public Let(Symbol[] syms, Node<?>[] lets, Node<T> bodyExpr) {
		super(bodyExpr.getDependencies().excludeAll(syms));
		nLets=syms.length;
		if (nLets!=lets.length) throw new IllegalArgumentException("Incorrect number of bindings forms for let");
		this.syms=syms;
		this.lets=lets;
		body=bodyExpr;
	}

	public static <T> Node<T> create(Node<?>[] body) {
		return create(RT.EMPTY_SYMBOLS,RT.EMPTY_NODES,body);
	}
	
	public static <T> Node<T> create(Symbol[] syms,Node<?>[] lets,Node<?>[] bodyExprs) {
		return new Let<T>(syms,lets,Do.create(bodyExprs));
	}
	
	public static <T> Node<T> create(Symbol[] syms,Node<?>[] lets,Node<T> bodyExpr) {
		return new Let<T>(syms,lets,bodyExpr);
	}
	
	@Override
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		for (int i=0; i<nLets; i++) {
			bindings=bindings.assoc(syms[i], (Object)(lets[i].compute(context,bindings)));
		}
		
		Result<T> r=body.eval(context,bindings);
		return r;
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<?>[] newLets=lets;
		boolean changed=false;
		for (int i=0; i<nLets; i++) {
			Node<?> node=lets[i];
			Node<?> newNode=node.specialiseValues(bindings);
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
		        bindings=bindings.dissoc(syms[i]); // binding no longer visible, must be calcyulated
			}
		}

		Node<T> newBody=body.specialiseValues(bindings);
		
		return ((body==newBody)&&(lets==newLets))?this:create(syms,newLets,newBody);
	}
	
	@Override
	public Node<T> optimise() {
		Node<?>[] newLets=lets;
		boolean changed=false;
		for (int i=0; i<nLets; i++) {
			Node<?> node=lets[i];
			Node<?> newNode=node.optimise();
			if (node!=newNode) {
				if (!changed) {
					newLets=newLets.clone();
					changed=true;
				}
				newLets[i]=newNode;
			} 
		}

		Node<T> newBody=body.optimise();
		if (body.isConstant()) return body;
		
		return ((body==newBody)&&(lets==newLets))?this:create(syms,newLets,newBody);
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("(Let [");
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
