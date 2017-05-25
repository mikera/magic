package magic.ast;

import java.util.List;

import magic.RT;
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
public class Vector<T> extends Node<APersistentVector<? extends T>> {

	APersistentVector<Node<? extends T>> exps;
	
	private Vector(APersistentVector<Node<? extends T>> exps, SourceInfo source) {
		super(calcDependencies(exps),source); 
		this.exps=(APersistentVector<Node<? extends T>>)exps;
	}
	

	public static <T> Vector<T> create(APersistentVector<Node<? extends T>> exps, SourceInfo source) {
		return (Vector<T>) new Vector<T>(exps,source);
	}
	
	public static <T> Vector<T> create(List<Node<? extends T>> list, SourceInfo source) {
		return create(Vectors.createFromList(list),source);
	}	

	@SuppressWarnings("unchecked")
	public static <T> Vector<T> create(magic.ast.List list, SourceInfo sourceInfo) {
		return (Vector<T>) create(list.getNodes(),sourceInfo);
	}

	public static <T> Vector<T> create(APersistentVector<Node<? extends T>> exps) {
		return create(exps,null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Vector<T> create(Node<? extends T>... exps) {
		return create(Vectors.createFromArray(exps),null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<APersistentVector<? extends T>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (APersistentVector<T>)Tuple.EMPTY);
		Object[] results=new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		APersistentVector <? extends T> r=(APersistentVector<? extends T>) Vectors.wrap(results);
		return EvalResult.create(c, r);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<Object> evalQuoted(Context c, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (APersistentVector<T>)Tuple.EMPTY);
		Object[] results=new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).evalQuoted(c,bindings,syntaxQuote);
		}
		return EvalResult.create(c, Vectors.wrap(results));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<APersistentVector<? extends T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		int nExps=exps.size();
		APersistentVector<Node<? extends T>> newExps=exps;
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
	public Node<APersistentVector<? extends T>> optimise() {
		int nExps=exps.size();
		APersistentVector<Node<? extends T>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.optimise();
			if (node!=newNode) {
				newExps=newExps.assocAt(i,(Node<T>) newNode);
			} 
		}
		return (exps==newExps)?this:(Vector<T>) create(newExps);
	}


	public IPersistentVector<Node<? extends T>> getExpressions() {
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
		StringBuilder sb=new StringBuilder("[");
		sb.append(RT.toString(exps, " "));
		sb.append(']');
		return sb.toString();
	}

	public int size() {
		return exps.size();
	}

	@SuppressWarnings("unchecked")
	public Node<T> get(int i) {
		return (Node<T>) exps.get(i);
	}

	public APersistentVector<Node<? extends T>> getNodes() {
		return exps;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public APersistentVector<? extends T> toForm() {
		return ((APersistentVector)exps).map(Nodes.TO_FORM);
	}








}
