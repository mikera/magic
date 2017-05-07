package magic.ast;

import magic.RT;
import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Let<T> extends Node<T> {
	private final int nLets;
	private final int nBody;
	private final Node<?>[] body;
	private final Symbol[] syms;
	private final Node<?>[] lets;
	
	public Let(Node<?>[] bodyExprs, Symbol[] syms, Node<?>[] lets) {
		super(calcDependencies(bodyExprs).excludeAll(syms));
		nLets=syms.length;
		nBody=bodyExprs.length;
		if (nLets!=lets.length) throw new IllegalArgumentException("Incorrect number of bindings forms for let");
		this.syms=syms;
		this.lets=lets;
		body=bodyExprs;
	}

	public static <T> Node<T> create(Node<?>[] body) {
		return create(RT.EMPTY_SYMBOLS,RT.EMPTY_NODES,body);
	}
	
	public static <T> Node<T> create(Symbol[] syms,Node<?>[] lets,Node<?>[] body) {
		return new Let<T>(body,syms,lets);
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		int nBody=body.length;
		
		for (int i=0; i<nLets; i++) {
			bindings=bindings.assoc(syms[i], (Object)(lets[i].compute(context)));
		}
		
		Result<T> r=new Result<>(context,null);
		for (int i=0; i<nBody; i++) {
			r=(Result<T>) body[i].eval(r.getContext(),bindings);
		}
		return r;
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<?>[] newBody=body;
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
		    bindings=bindings.dissoc(syms[i]); // binding no longer visible
		}
		changed=false; // reset changed for body
		for (int i=0; i<nBody; i++) {
			Node<?> node=body[i];
			Node<?> newNode=node.specialiseValues(bindings);
			if (node!=newNode) {
				if (!changed) {
					newBody=newBody.clone();
					changed=true;
				}
				newBody[i]=newNode;
			} 
		}
		
		return ((body==newBody)&&(lets==newLets))?this:create(syms,newLets,newBody);
	}

}
