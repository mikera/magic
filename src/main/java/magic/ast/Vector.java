package magic.ast;

import magic.compiler.Result;
import magic.data.APersistentMap;
import magic.data.IPersistentVector;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.data.Vectors;
import magic.lang.Context;

/**
 * Expression class representing a vector construction literal.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Vector<T> extends Node<IPersistentVector<T>> {

	IPersistentVector<Node<T>> exps;
	
	private Vector(IPersistentVector<Node<T>> exps) {
		super(calcDependencies(exps)); 
		this.exps=exps;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Result<IPersistentVector<T>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  Result.create(c, (IPersistentVector<T>)Tuple.EMPTY);
		T[] results=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		return Result.create(c, (IPersistentVector<T>)Vectors.wrap(results));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<IPersistentVector<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		int nExps=exps.size();
		IPersistentVector<Node<T>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.specialiseValues(bindings);
			if (node!=newNode) {
				newExps=newExps.update(i,(Node<T>) newNode);
			} 
		}
		return (exps==newExps)?this:create(exps);
	}

	public static <T> Vector<T> create(IPersistentVector<Node<T>> exps) {
		return new Vector<T>(exps);
	}

	public IPersistentVector<Node<T>> getExpressions() {
		return exps;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("(Vector");
		int n=exps.size();
		for (int i=0; i<n; i++) {
			sb.append(' ');
			sb.append(exps.get(i).toString());
		}
		sb.append(')');
		return sb.toString();
	}
}
