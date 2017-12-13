package magic.ast;
import java.util.Collection;

import org.objectweb.asm.commons.GeneratorAdapter;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;

import magic.Keywords;
import magic.RT;
import magic.Type;
import magic.Types;
import magic.compiler.Analyser;
import magic.compiler.AnalysisContext;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSequence;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.PersistentHashMap;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.data.impl.NullSet;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.MagicLanguage;
 
/**
 * Abstract base class for expressions AST nodes
 * 
 * @author Mike
 *
 */
@NodeInfo(language = "Magic", description = "The abstract base node for all expressions")
public abstract class Node<T> extends RootNode {

	public static final Node<?>[] EMPTY_ARRAY = new Node[0];
	
	private final APersistentMap<Keyword,Object> meta;
		
	@SuppressWarnings("deprecation")
	public Node(APersistentMap<Keyword, Object> meta) {
		super(MagicLanguage.class, null,null);
		this.meta=meta;
	}

	protected Node<?> updateMeta() {
		APersistentMap<Keyword, Object> depMeta=includeMetaDependencies(meta);
		if (depMeta!=meta) return withMeta(depMeta);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	protected final APersistentMap<Keyword, Object> includeMetaDependencies(APersistentMap<Keyword, Object> meta) {
		APersistentSet<Symbol> deps=(APersistentSet<Symbol>) meta.get(Keywords.DEPS);
		if (deps==null) deps=Sets.emptySet();
		deps=this.includeDependencies(deps);
		if (deps==null) throw new Error("Null deps generated in: "+this +" with class "+this.getClass());
		if (deps.isEmpty()) return meta;
		return meta.assoc(Keywords.DEPS,deps);
	}

	/**
	 * Augments the set of dependencies with dependencies from this node.
	 * If called at the end of analysis to perform dependency updates if necessary.
	 * Function intended to be overriden. 
	 * @param deps
	 * @return
	 */
	protected abstract APersistentSet<Symbol> includeDependencies(APersistentSet<Symbol> deps);

	/**
	 * Creates a raw AST node tree from the given form
	 * @param form
	 * @return
	 */
	public static <T> Node<T> toNode(Object form) {
		return Analyser.analyse(form);
	}

	/**
	 * Get the metadata associated with this Node
	 * @return
	 */
	public APersistentMap<Keyword,Object> meta() {
		return meta;
	}

	/**
	 * Returns this node with updated metadata
	 * @param meta
	 * @return
	 */
	public abstract Node<T> withMeta(APersistentMap<Keyword,Object> meta);

	public Node<T> assocMeta(Keyword k,Object v) {
		return withMeta(meta.assoc(k,v));
	}

	
	@SuppressWarnings("unchecked")
	public final T compute(Context c) {
		return compute(c,(PersistentHashMap<Symbol, Object>) PersistentHashMap.EMPTY);
	}

	/**
	 * Computes the value of an expression  node in the given context
	 * Discards any effects on the context
	 * @param c
	 * @param bindings
	 * @return
	 */
	public final T compute(Context c, APersistentMap<Symbol,Object> bindings) {
		return eval(c,bindings).getValue();
	}
	
	@Override
	public Object execute(VirtualFrame virtualFrame) {
		throw new UnsupportedOperationException("Can't execute a Magic node without a context");
	}
	
	/**
	 * TODO: docs for emit
	 * @param c
	 * @param bindings
	 */
	public void emit(Context c,APersistentMap<Symbol,Object> bindings, GeneratorAdapter gen) {
		throw new UnsupportedOperationException("Can't emit code for node of type: "+this.getClass());
	}

	/**
	 * Returns the value of this Node. Throws an error if the node does not have a constant value.
	 * @return
	 */
	public T getValue() {
		throw new UnsupportedOperationException("Cannot use getValue() on a non-constant expression");
	}
	
	public APersistentSequence<Node<?>> getNodes() {
		throw new UnsupportedOperationException("getNodes not supported on node of type "+this.getClass());
	}
	
	/**
	 * Returns true if this node is provably a constant value
	 * @return
	 */
	public boolean isConstant() {
		return false;
	}
	
	/**
	 * Returns true if this node is provably a constant Symbol
	 * @return
	 */
	public boolean isSymbol() {
		return false;
	}
	
	/**
	 * Returns true if this node is a keyword constant
	 * @return
	 */
	public boolean isKeyword() {
		return false;
	}
	
	/**
	 * Get the Symbol representing this node.
	 * 
	 * Throws an error if the node is not a symbol.
	 * @return
	 */
	public Symbol getSymbol() {
		return (Symbol)getValue();
	}
	
	@SuppressWarnings("unchecked")
	public final APersistentSet<Symbol> getDependencies() {
		APersistentSet<Symbol> deps=(APersistentSet<Symbol>) meta.get(Keywords.DEPS);
		if (deps==null) return Sets.emptySet();
		return deps;
	}
	
	protected static APersistentSet<Symbol> calcDependencies(Node<?> f, Node<?>[] args) {
		APersistentSet<Symbol> deps=f.getDependencies();
		deps=deps.includeAll(calcDependencies(args));
		return deps;
	}
	
	/**
	 * Applies a function to all regular child nodes of this node.
	 * Returns the same Node if unchanged.
	 * @param fn
	 * @return
	 */
	public Node<?> mapChildren(IFn1<Node<?>,Node<?>> fn) {
		throw new UnsupportedOperationException("mapChildren not supported for class "+RT.className(this));
	}
	
	@SafeVarargs
	protected static <T> APersistentSet<Symbol> calcDependencies(Node<? extends T>... nodes) {
		APersistentSet<Symbol> deps=Sets.emptySet();
		for (int i=0; i<nodes.length; i++) {
			deps=deps.includeAll(nodes[i].getDependencies());
		}
		return deps;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T> APersistentSet<Symbol> calcDependencies(APersistentVector<Node<? extends T>> nodes) {
		APersistentSet<Symbol> deps=(APersistentSet<Symbol>) NullSet.INSTANCE;
		int n=nodes.size();
		for (int i=0; i<n; i++) {
			Node<?> node=nodes.get(i);
			APersistentSet<Symbol> newDeps=node.getDependencies();
			deps=deps.includeAll(newDeps);
		}
		return deps;
	}
	
	protected static <T> APersistentSet<Symbol> calcDependencies(APersistentList<Node<? extends T>> nodes) {
		return calcDependencies(Vectors.coerce(nodes));
	}
	
	/**
	 * Gets the Magic Type for this expression node.
	 * 
	 * Returns the type as computed in the node metadata, or Types.ANY if not defined.
	 */
	public Type getType() {
		Type t=(Type)meta.get(Keywords.TYPE);
		if (t!=null) return t;
		return Types.ANY;
	}
	
	/**
	 * Gets source information for this node, or null if not abvailable
	 * @return
	 */
	public SourceInfo getSourceInfo() {
		try {
			return (SourceInfo) meta.get(Keywords.SOURCE);
		} catch (Throwable t) {
			System.err.println("Problem getting source info for node: "+this+" with meta " +meta());
			throw t;
		}
	}

	/**
	 * Evaluates the node in the given context, returning an updated context and value
	 * @param context
	 * @param bindings 
	 * @return
	 */
	public abstract EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings);

	/** 
	 * Specialises a node with a set of bindings.
	 * 
	 * Should be context aware... i.e. only specialising nodes that genuinely reference the lexical
	 * context.
	 * 
	 * @param bindings
	 * @return
	 */
	public abstract Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings);
	
	/** 
	 * Performs local optimisations on the node prior to execution / compilation.
	 * 
	 * Should be run after analysis on runnable code only.
	 *  
	 * Returns a new node if any optimisation succeeded, the same node otherwise.
	 * 
	 * @param bindings
	 * @return
	 */
	public abstract Node<? extends T> optimise();
	
	
	/** 
	 * Converts this AST node to a form data representation
	 * @return
	 */
	public abstract Object toForm();
	
	@Override
	public abstract String toString();

	/**
	 * Evaluates this Node in a quoted context, returning a Node object
	 * 
	 * @param context
	 * @param bindings
	 * @return
	 */
	public abstract Node<?> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings, boolean syntaxQuote);

	/**
	 * Validation function. Override to perform validation tests.
	 * Should throw informative errors in the case of any validation failure
	 */
	public void validate() {
		if (getDependencies()==null) throw new Error("Null dependencies?");
		if (getType()==null) throw new Error("Null type?");
	}

	/**
	 * Analyses a node in the given context.
	 * 
	 * Analysis basically operates as follows:
	 * 1. Analyse children (if any)
	 * 2. Compute updated metadata (if needed)
	 * 
	 * Returns a fully analysed node, ready for compilation or execution.
	 * 
	 * @param context
	 * @return
	 */
	public Node<?> analyse(AnalysisContext context) {
		Node<?> newNode=mapChildren(NodeFunctions.analyse(context));
		return newNode.updateMeta();
	}


	/**
	 * Updates this node, including the specified symbolic dependency
	 * 
	 * @param sym
	 * @return
	 */
	public Node<T> withDependency(Symbol sym) {
		APersistentSet<Symbol> deps=getDependencies();
		if (deps.containsKey(sym)) return this;
		return withMeta(meta.assoc(Keywords.DEPS, deps.include(sym)));
	}
	
	/**
	 * Updates this node, including the specified symbolic dependency
	 * 
	 * @param sym
	 * @return
	 */
	public Node<T> withDependencies(Collection<Symbol> syms) {
		APersistentSet<Symbol> deps=getDependencies();
		APersistentSet<Symbol> newDeps=deps.includeAll(syms);
		if (deps==newDeps) return this;
		return withMeta(meta.assoc(Keywords.DEPS, newDeps));
	}

	

}
