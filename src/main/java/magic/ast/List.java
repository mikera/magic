package magic.ast;

import magic.RT;
import magic.compiler.Analyser;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Lists;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * AST node representing a list
 * 
 * This is an interim AST data structure: we expect to transform into e.g. an Apply node
 * 
 * @author Mike
 *
 * @param <T>
 */
public class List extends BaseForm<Object> {
	/**
	 * An empty list node with no source information
	 */
	public static final List EMPTY = create(Node.EMPTY_ARRAY);

	private List(APersistentList<Node<? extends Object>> nodes, APersistentSet<Symbol> deps, SourceInfo source) {
		super(nodes, deps, source);
	}
	
	public static List create(Node<?>[] nodes, SourceInfo sourceInfo) {
		return create((APersistentList<Node<?>>)Lists.wrap(nodes),sourceInfo);
	}

	public static List create(Node<?>[] nodes) {
		return create(nodes,(SourceInfo)null);
	}

	public static List create(APersistentList<Node<?>> nodes,SourceInfo source) {
		// get deps from first element in list of present
		APersistentSet<Symbol> deps=(nodes.size()==0)?Sets.emptySet():nodes.get(0).getDependencies();
		return new List(nodes,deps,source);
	}
	
	public static List create(List a,SourceInfo source) {
		return create(a.getNodes(),source);
	}
	
	public static List createCons(Node<?> a,List rest,SourceInfo source) {
		return create((APersistentList<Node<? extends Object>>) Lists.cons(a, rest.nodes),source);
	}
	
	public static List createCons(Node<?> a,Node<?> b,List rest,SourceInfo source) {
		return create((APersistentList<Node<? extends Object>>) Lists.cons(a, b,rest.nodes),source);
	}

	public static List createCons(Node<?> a,Node<?> b,Node<?> c,List rest,SourceInfo source) {
		return create((APersistentList<Node<? extends Object>>) Lists.cons(a, b,c, rest.nodes),source);
	}

	
	@Override
	public Node<Object> specialiseValues(APersistentMap<Symbol,Object> bindings) {
		APersistentList<Node<? extends Object>> newNodes=nodes;
		int n=nodes.size();
		for (int i=0; i<n; i++) {
			Node<?> node=nodes.get(i);
			Node<?> newNode=node.specialiseValues(bindings);
			if (node!=newNode) newNodes=newNodes.assocAt(i, newNode);
		}
		if (newNodes==nodes) return this;
		return create(newNodes,source);
	}
	
	@Override
	public EvalResult<Object> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		if (size()==0) return new EvalResult<Object>(context,Lists.EMPTY);
		throw new UnsupportedOperationException("Cannot compile node of type: "+this.getClass());
	}
	
	@Override
	public Node<?> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings, boolean syntaxQuote) {
		if ((size()==2)) {
			Node<?> h=get(0);
			if (h.isSymbol()&&(h.getSymbol().equals(Symbols.UNQUOTE))) {
				Node<?> ex=get(1);
				Object form=ex.compute(context, bindings);
				return Analyser.analyse(form);
			}
		};
		// TODO: shouldn't we be recursively evalQuoting?
		return this;
	}

	@Override
	public Node<Object> optimise() {
		return this;
	}

	@Override
	public String toString() {
		return "("+RT.toString(nodes," ")+")";
	}
	
	public int size() {
		return nodes.size();
	}

	@SuppressWarnings("unchecked")
	public Node<Object> get(int i) {
		return (Node<Object>) nodes.get(i);
	}

	public APersistentList<Node<? extends Object>> getNodes() {
		return nodes;
	}

	public List subList(int start, int end) {
		return List.create(nodes.subList(start, end),getSourceInfo());
	}

}
