package magic.ast;

import java.util.List;

import magic.RT;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.data.Vectors;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node class representing a vector construction literal.
 * 
 * @author Mike
 *
 * @param <T> the type of all nodes in the Vector
 */
public class Vector<T> extends BaseDataStructure<APersistentVector<? extends T>> {

	private Vector(APersistentVector<Node<?>> exps, SourceInfo source) {
		super((APersistentVector<Node<?>>)exps,calcDependencies(exps),source); 
	}

	public static <T> Vector<T> create(APersistentVector<Node<?>> exps, SourceInfo source) {
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
	public Node<?> evalQuoted(Context c, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=exps.size();
		if (n==0) return  Constant.create((APersistentVector<T>)Tuple.EMPTY,getSourceInfo());
		Node<?>[] results=new Node[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).evalQuoted(c,bindings,syntaxQuote);
		}
		return Vector.create(Vectors.wrap(results),getSourceInfo());
	}
	
	@Override
	public Node<APersistentVector<? extends T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	
	@Override
	public Node<APersistentVector<? extends T>> optimise() {
		if (size()==0) return Constant.create(Vectors.emptyVector());
		return mapChildren(NodeFunctions.optimise());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Vector<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentVector<Node<?>> newExps=NodeFunctions.mapAll(exps, fn);
		return (exps==newExps)?this:(Vector<T>) create(newExps);
	}
	
	/**
	 * Gets the Type of this vector expression
	 */
	@Override
	public Type getType() {
		//TODO: include length info in type?
		return Types.VECTOR;
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

	public APersistentVector<Node<?>> getNodes() {
		return exps;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public APersistentVector<? super T> toForm() {
		return ((APersistentVector)exps).map(NodeFunctions.TO_FORM);
	}


}
