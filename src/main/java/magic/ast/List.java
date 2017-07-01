package magic.ast;

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
import magic.data.Tuple;
import magic.data.Vectors;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node class representing a list construction literal.
 * 
 * @author Mike
 *
 * @param <T> the type of all nodes in the List
 */
public class List<T> extends BaseDataStructure<APersistentList<? extends T>> {

	private List(APersistentVector<Node<?>> exps, APersistentMap<Keyword, Object> meta) {
		super((APersistentVector<Node<?>>)exps,meta); 
	}

	@Override
	public List<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new List<T>(exps,meta);
	}
	
	public static <T> List<T> create(APersistentVector<Node<? extends T>> list, SourceInfo source) {
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE, source);
		APersistentSet<Symbol> deps=calcDependencies(list);
		meta=meta.assoc(Keywords.DEPS, deps);
		return new List<T>(Vectors.createFromList(list),meta);
	}	

	@SuppressWarnings("unchecked")
	public static <T> List<T> create(magic.ast.ListForm list, SourceInfo sourceInfo) {
		return (List<T>) create(Vectors.coerce(list.getNodes()),sourceInfo);
	}

	public static <T> List<T> create(APersistentVector<Node<? extends T>> exps) {
		return create(exps,null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> create(Node<? extends T>... exps) {
		return create(Vectors.createFromArray(exps),null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<APersistentList<? extends T>> eval(Context c,APersistentMap<Symbol, Object> bindings) {
		int n=exps.size();
		if (n==0) return  EvalResult.create(c, (APersistentList<T>)Lists.EMPTY);
		Object[] results=new Object[n];
		for (int i=0; i<n; i++) {
			results[i]=exps.get(i).compute(c,bindings);
		}
		APersistentList <? extends T> r=(APersistentList<? extends T>) Lists.wrap(results);
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
		return List.create(Vectors.wrap(results),getSourceInfo());
	}
	
	@Override
	public Node<APersistentList<? extends T>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends APersistentList<? extends T>> optimise() {
		if (size()==0) return (Node<? extends APersistentList<? extends T>>) Constant.create((APersistentList<T>)Lists.EMPTY,getSourceInfo());
		List<T> mapped= mapChildren(NodeFunctions.optimise());
		return (Node<? extends APersistentList<? extends T>>) mapped.optimiseLocal();
	}
	
	@SuppressWarnings("unchecked")
	private Node<?> optimiseLocal() {
		int n=size();
		for (int i=0; i<n; i++) {
			if (!exps.get(i).isConstant()) return this;
		}
		return Constant.create(exps.map(node -> ((Node<T>)node).getValue()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentVector<Node<?>> newExps=NodeFunctions.mapAll(exps, fn);
		return (exps==newExps)?this:(List<T>) create(newExps);
	}
	
	/**
	 * Gets the Type of this list expression
	 */
	@Override
	public Type getType() {
		//TODO: include length info in type?
		return Types.LIST;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("(list ");
		sb.append(RT.toString(exps, " "));
		sb.append(')');
		return sb.toString();
	}

	public int size() {
		return exps.size();
	}

	@SuppressWarnings("unchecked")
	public Node<T> get(int i) {
		return (Node<T>) exps.get(i);
	}

	@Override
	public APersistentList<? super T> toForm() {
		return Lists.cons(Symbols.LIST, Lists.coerce(exps.map(NodeFunctions.TO_FORM)));
	}



}
