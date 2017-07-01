package magic.ast;

import java.util.List;

import magic.Keywords;
import magic.RT;
import magic.Symbols;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node class representing a hashmap construction literal.
 * 
 * @author Mike
 *
 * @param <K> the type of all keys in the hashmap
 * @param <V> the type of all values in the hashmap
 */
public class HashMap<K,V> extends BaseDataStructure<APersistentMap<? extends K,? extends V>> {

	private HashMap(APersistentVector<Node<?>> exps, APersistentMap<Keyword,Object> meta) {
		super(exps,meta); 
	}
	
	@Override
	public HashMap<K,V> withMeta(APersistentMap<Keyword, Object> meta) {
		return new HashMap<K, V>(exps,meta);
	}

	public static <K,V> HashMap<K,V> create(APersistentVector<Node<?>> exps, APersistentSet<Symbol> deps, SourceInfo source) {
		APersistentMap<Keyword,Object> meta=Maps.create(Keywords.SOURCE, source);
		meta=meta.assoc(Keywords.DEPS, deps);
		return (HashMap<K,V>) new HashMap<K,V>(exps,meta);
	}
	
	public static <K,V> HashMap<K,V> create(APersistentVector<Node<?>> exps, SourceInfo source) {
		return create(exps,calcDependencies(exps),source);
	}
	
	public static <K,V> HashMap<K,V> create(List<Node<?>> list, SourceInfo source) {
		return create(Vectors.createFromList(list),source);
	}	

	public static <K,V> HashMap<K,V> create(magic.ast.ListForm list, SourceInfo sourceInfo) {
		return create(list.getNodes(),sourceInfo);
	}

	public static <K,V> HashMap<K,V> create(APersistentVector<Node<?>> exps) {
		return create(exps,null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<APersistentMap<? extends K, ? extends V>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (APersistentMap<K,V>)Maps.EMPTY);
		Object[] results=new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		APersistentMap<K,V> r=(APersistentMap<K, V>) Maps.createFromFlattenedArray(results);
		return EvalResult.create(c, r);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<?> evalQuoted(Context c, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		int nExps=exps.size();
		APersistentVector<Node<?>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.evalQuoted(c,bindings,syntaxQuote);
			if (node!=newNode) {
				// System.out.println("Specialising "+node+ " to "+newNode);
				newExps=newExps.assocAt(i, newNode);
			} 
		}
		return (exps==newExps)?this:(HashMap<K,V>) create(newExps,getSourceInfo());
	}
	
	@Override
	public Node<APersistentMap<? extends K, ? extends V>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	
	@Override
	public Node<APersistentMap<? extends K, ? extends V>> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentVector<Node<?>> newNodes=NodeFunctions.mapAll(exps,fn);
		if (newNodes==exps) return this;
		return create(newNodes,getSourceInfo());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<APersistentMap<? extends K, ? extends V>> optimise() {
		int nExps=exps.size();
		APersistentVector<Node<?>> newExps=exps;
		boolean constant=true;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=node.optimise();
			if (node!=newNode) {
				newExps=newExps.assocAt(i,newNode);
			} 
			if (!node.isConstant()) constant=false;
		}
		
		// can optimise to a constant hashmap
		if (constant) {
			APersistentVector<Object> vals=newExps.map(n->((Node<?>)n).getValue());
			return Constant.create((APersistentMap<K, V>)Maps.createFromFlattenedPairs(vals), getDependencies());
		}
		
		return (exps==newExps)?this:(HashMap<K,V>) create(newExps,getSourceInfo());
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
		StringBuilder sb=new StringBuilder("{");
		sb.append(RT.toString(exps, " "));
		sb.append('}');
		return sb.toString();
	}

	public int size() {
		return exps.size();
	}

	public Node<?> get(int i) {
		return exps.get(i);
	}

	@Override
	public APersistentList<?> toForm() {
		return Lists.cons(Symbols.HASHMAP, Lists.create(exps.map(NodeFunctions.TO_FORM)));
	}

	

}
