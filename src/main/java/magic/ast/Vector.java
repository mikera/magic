package magic.ast;

import magic.Type;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.IPersistentVector;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.data.Vectors;
import magic.lang.Context;
import magic.type.JavaType;

/**
 * AST node class representing a vector construction literal.
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
	public EvalResult<IPersistentVector<T>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (IPersistentVector<T>)Tuple.EMPTY);
		T[] results=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		return EvalResult.create(c, (IPersistentVector<T>)Vectors.wrap(results));
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
				// System.out.println("Specialising "+node+ " to "+newNode);
				newExps=newExps.assocAt(i,(Node<T>) newNode);
			} 
		}
		return (exps==newExps)?this:create(newExps);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<IPersistentVector<T>> optimise() {
		int nExps=exps.size();
		IPersistentVector<Node<T>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.optimise();
			if (node!=newNode) {
				newExps=newExps.assocAt(i,(Node<T>) newNode);
			} 
		}
		return (exps==newExps)?this:create(newExps);
	}

	public static <T> Vector<T> create(IPersistentVector<Node<T>> exps) {
		return new Vector<T>(exps);
	}

	public IPersistentVector<Node<T>> getExpressions() {
		return exps;
	}
	
	/**
	 * Gets the Type of this vector expression
	 */
	@Override
	public Type getType() {
		//TODO: include length info in type?
		return JavaType.create(APersistentVector.class);
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
