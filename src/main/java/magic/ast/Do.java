package magic.ast;

import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

public class Do<T> extends Node<T> {
	private final int nBody;
	private final Node<?>[] body;
	
	public Do(Node<?>[] bodyExprs) {
		super(calcDependencies(bodyExprs));
		nBody=bodyExprs.length;
		body=bodyExprs;
	}

	public static <T> Node<T> create(Node<?>[] body) {
		return new Do<T>(body);
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		int nBody=body.length;
		
		Result<T> r=new Result<>(context,null);
		for (int i=0; i<nBody; i++) {
			r=(Result<T>) body[i].eval(r.getContext(),bindings);
		}
		return r;
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		boolean changed=false;
		Node<?>[] newBody=body;
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
		return (body==newBody)?this:create(newBody);
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("(Do ");
		for (int i=0; i<nBody; i++) {
			if (i>0) sb.append(' ');
			sb.append(body[i]);
		}
		sb.append(')');
		return sb.toString();
	}

}
