package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;

/**
 * AST node representing a list
 * 
 * This is an interim AST data structure: we expect to transform into e.g. an Apply node
 * 
 * @author Mike
 *
 * @param <T>
 */
public class List<T> extends BaseForm<T> {
	private List(APersistentList<Node<? extends Object>> nodes, APersistentSet<Symbol> deps, SourceInfo source) {
		super(nodes, deps, source);
	}
	
	private List(APersistentList<Node<? extends Object>> nodes, SourceInfo source) {
		this(nodes, calcDependencies(nodes), source);
	}
	
	public static <T> Node<T> create(Node<?>[] nodes) {
		return create((APersistentList<Node<?>>)Lists.wrap(nodes),(SourceInfo)null);
	}

	public static <T> Node<T> create(APersistentList<Node<? extends Object>> nodes,SourceInfo source) {
		return new List<T>(nodes,source);
	}
	
	public static <T> Node<T> create(List<? extends T> a,SourceInfo source) {
		return create(a.nodes,source);
	}
	
	public static <T> Node<T> createCons(Node<? extends Object> a,List<? extends T> b,SourceInfo source) {
		return create(Lists.cons(a, b.nodes),source);
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol,Object> bindings) {
		APersistentList<Node<? extends Object>> newNodes=nodes;
		int n=nodes.size();
		for (int i=0; i<n; i++) {
			Node<?> node=nodes.get(i);
			Node<?> newNode=node.specialiseValues(bindings);
			if (node!=newNode) newNodes=newNodes.assocAt(i, newNode);
		}
		if (newNodes==nodes) return this;
		return new List<T>(newNodes,source);
	}

	@Override
	public Node<T> optimise() {
		return this;
	}

	@Override
	public String toString() {
		return "(List "+RT.toString(nodes," ")+")";
	}

	@Override
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol,Object> bindings, boolean syntaxQuote) {
		int n=nodes.size();
		Object[] vs=new Object[n];
		for (int i=0; i<n; i++) {
			EvalResult<Object> r=nodes.get(i).evalQuoted(context, bindings, syntaxQuote);
			vs[i]=r.getValue();
			context=r.getContext();
		}
		return new EvalResult<Object>(context,PersistentList.wrap(vs));
	}
	
	public int size() {
		return nodes.size();
	}

	@SuppressWarnings("unchecked")
	public Node<T> get(int i) {
		return (Node<T>) nodes.get(i);
	}

}
