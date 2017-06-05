package magic.ast;

import java.util.List;

import magic.RT;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.data.Vectors;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * AST node class representing a vector construction literal.
 * 
 * @author Mike
 *
 * @param <T> the type of all nodes in the Vector
 */
public class Set<T> extends BaseDataStructure<APersistentSet<? extends T>> {

	private Set(APersistentVector<Node<?>> exps, SourceInfo source) {
		super((APersistentVector<Node<?>>)exps,calcDependencies(exps),source); 
	}

	public static <T> Set<T> create(APersistentVector<Node<?>> exps, SourceInfo source) {
		return (Set<T>) new Set<T>(exps,source);
	}
	
	public static <T> Set<T> create(List<Node<? extends T>> list, SourceInfo source) {
		return create(Vectors.createFromList(list),source);
	}	

	@SuppressWarnings("unchecked")
	public static <T> Set<T> create(magic.ast.List list, SourceInfo sourceInfo) {
		return (Set<T>) create(list.getNodes(),sourceInfo);
	}

	public static <T> Set<T> create(APersistentVector<Node<? extends T>> exps) {
		return create(exps,null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T> create(Node<? extends T>... exps) {
		return create(Vectors.createFromArray(exps),null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<APersistentSet<? extends T>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (APersistentSet<T>)Sets.emptySet());
		Object[] results=new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		APersistentSet <? extends T> r=(APersistentSet<? extends T>)Sets.createFrom(results);
		return EvalResult.create(c, r);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<?> evalQuoted(Context c, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int n=exps.size();
		if (n==0) return  Constant.create((APersistentVector<T>)Tuple.EMPTY);
		Node<?>[] results=new Node[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).evalQuoted(c,bindings,syntaxQuote);
		}
		return create(Vectors.wrap(results),getSourceInfo());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<APersistentSet<? extends T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		int nExps=exps.size();
		APersistentVector<Node<?>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.specialiseValues(bindings);
			if (node!=newNode) {
				// System.out.println("Specialising "+node+ " to "+newNode);
				newExps=newExps.assocAt(i,(Node<T>) newNode);
			} 
		}
		return (exps==newExps)?this:(Set<T>) create(newExps,getSourceInfo());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<APersistentSet<? extends T>> optimise() {
		int nExps=exps.size();
		APersistentVector<Node<?>> newExps=exps;
		boolean constant=true;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.optimise();
			if (node!=newNode) {
				newExps=newExps.assocAt(i,(Node<T>) newNode);
			} 
			if (!node.isConstant()) constant=false;
		}
		
		// can optimise to a constant set
		if (constant) {
			APersistentVector<T> vals=newExps.map(n->((Node<T>)n).getValue());
			return Constant.create(Sets.createFrom(vals), deps);
		}
		
		return (exps==newExps)?this:(Set<T>) create(newExps,getSourceInfo());
	}
	
	/**
	 * Gets the Type of this vector expression
	 */
	@Override
	public Type getType() {
		return Types.SET;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("#{");
		sb.append(RT.toString(exps, " "));
		sb.append('}');
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
	public APersistentList<?> toForm() {
		return Lists.cons(Symbols.SET, Lists.create(((APersistentVector)exps).map(Nodes.TO_FORM)));
	}

}
