package magic.ast;

import java.util.List;

import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
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
 * @param <T> the type of all nodes in the Vector
 */
public class Vector<T> extends Node<APersistentVector<T>> {

	APersistentVector<Node<T>> exps;
	
	private Vector(APersistentVector<Node<T>> exps, SourceInfo source) {
		super(calcDependencies(exps),source); 
		this.exps=exps;
	}
	

	public static <T> Vector<T> create(APersistentVector<Node<T>> exps, SourceInfo source) {
		return new Vector<T>(exps,source);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Vector<T> create(List<?> list, SourceInfo source) {
		return create((APersistentVector<Node<T>>)Vectors.createFromList(list),source);
	}

	public static <T> Vector<T> create(APersistentVector<Node<T>> exps) {
		return create(exps,null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Vector<T> create(Node<T>... exps) {
		return create(Vectors.createFromArray(exps),null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<APersistentVector<T>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (APersistentVector<T>)Tuple.EMPTY);
		T[] results=(T[]) new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		return EvalResult.create(c, Vectors.wrap(results));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<APersistentVector<T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		int nExps=exps.size();
		APersistentVector<Node<T>> newExps=exps;
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
	public Node<APersistentVector<T>> optimise() {
		int nExps=exps.size();
		APersistentVector<Node<T>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.optimise();
			if (node!=newNode) {
				newExps=newExps.assocAt(i,(Node<T>) newNode);
			} 
		}
		return (exps==newExps)?this:create(newExps);
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


	public int size() {
		return exps.size();
	}


	public Node<T> get(int i) {
		return exps.get(i);
	}

	public APersistentVector<Node<T>> getNodes() {
		return exps;
	}




}
