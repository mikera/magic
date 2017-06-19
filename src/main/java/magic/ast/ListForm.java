package magic.ast;

import magic.RT;
import magic.compiler.Analyser;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Sets;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Keywords;
import magic.lang.Symbols;

/**
 * AST node representing a form as a list
 * 
 * This is an interim AST data structure: we expect to transform this into e.g. an Apply node
 * 
 * @author Mike
 *
 * @param <T>
 */
public class ListForm extends BaseForm<Object> {
	/**
	 * An empty list node with no source information
	 */
	public static final ListForm EMPTY = create(Node.EMPTY_ARRAY);

	private ListForm(APersistentList<Node<? extends Object>> nodes, APersistentMap<Keyword, Object> meta) {
		super(nodes, meta);
	}
	
	@Override
	public Node<Object> withMeta(APersistentMap<Keyword, Object> meta) {
		return new ListForm(nodes,meta);
	}

	
	public static ListForm create(Node<?>[] nodes, SourceInfo sourceInfo) {
		return create((APersistentList<Node<?>>)Lists.wrap(nodes),sourceInfo);
	}

	public static ListForm create(Node<?>[] nodes) {
		return create(nodes,(SourceInfo)null);
	}

	public static ListForm create(APersistentList<Node<?>> nodes,SourceInfo source) {
		// get deps from first element in list of present
		APersistentSet<Symbol> deps=(nodes.size()==0)?Sets.emptySet():nodes.get(0).getDependencies();
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE,source);
		meta=meta.assoc(Keywords.DEPS, deps);
		return new ListForm(nodes,meta);
	}
	
	public static ListForm create(ListForm a,SourceInfo source) {
		return create(a.getNodes(),source);
	}
	
	public static ListForm createCons(Node<?> a,ListForm rest,SourceInfo source) {
		return create((APersistentList<Node<? extends Object>>) Lists.cons(a, rest.nodes),source);
	}
	
	public static ListForm createCons(Node<?> a,Node<?> b,ListForm rest,SourceInfo source) {
		return create((APersistentList<Node<? extends Object>>) Lists.cons(a, b,rest.nodes),source);
	}

	public static ListForm createCons(Node<?> a,Node<?> b,Node<?> c,ListForm rest,SourceInfo source) {
		return create((APersistentList<Node<? extends Object>>) Lists.cons(a, b,c, rest.nodes),source);
	}

	@Override
	public Node<Object> specialiseValues(APersistentMap<Symbol,Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	

	@Override
	public Node<Object> optimise() {
		if (size()==0) return Constant.create(Lists.EMPTY);
		return mapChildren(NodeFunctions.optimise());
	}
	
	@Override
	public Node<Object> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentList<Node<? extends Object>> newNodes=NodeFunctions.mapAll(nodes,fn);
		if (newNodes==nodes) return this;
		return create(newNodes,getSourceInfo());
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

	public ListForm subList(int start, int end) {
		return ListForm.create(nodes.subList(start, end),getSourceInfo());
	}

}
