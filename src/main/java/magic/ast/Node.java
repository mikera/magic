package magic.ast;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.PersistentHashMap;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Vectors;
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
	
	protected APersistentSet<Symbol> deps;
	protected SourceInfo source;

	public Node(APersistentSet<Symbol> deps, SourceInfo source) {
		this(MagicLanguage.class,null,null);
		this.deps=deps;
		this.source=source;
	}
	
	public Node(Class<? extends TruffleLanguage<?>> language, SourceSection sourceSection,
			FrameDescriptor frameDescriptor) {
		super(language, sourceSection, frameDescriptor);
	}

	@SuppressWarnings("unchecked")
	public final T compute(Context c) {
		return compute(c,(PersistentHashMap<Symbol, Object>) PersistentHashMap.EMPTY);
	}

	/**
	 * Computes the value of an expression  node in the given context
	 * Discards and effects on the context
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
	
	public T getValue() {
		throw new UnsupportedOperationException("Cannot use getValue() on a non-constant expression");
	}
	
	public boolean isConstant() {
		return false;
	}
	
	public APersistentSet<Symbol> getDependencies() {
		return deps;
	}
	
	protected static APersistentSet<Symbol> calcDependencies(Node<?> f, Node<?>[] args) {
		APersistentSet<Symbol> deps=f.getDependencies();
		deps=deps.includeAll(calcDependencies(args));
		return deps;
	}
	
	@SafeVarargs
	protected static <T> APersistentSet<Symbol> calcDependencies(Node<? extends T>... nodes) {
		APersistentSet<Symbol> deps=Sets.emptySet();
		for (int i=0; i<nodes.length; i++) {
			deps=deps.includeAll(nodes[i].getDependencies());
		}
		return deps;
	}
	
	protected static <T> APersistentSet<Symbol> calcDependencies(APersistentVector<Node<? extends T>> nodes) {
		APersistentSet<Symbol> deps=Sets.emptySet();
		int n=nodes.size();
		for (int i=0; i<n; i++) {
			deps=deps.includeAll(nodes.get(i).getDependencies());
		}
		return deps;
	}
	
	protected static <T> APersistentSet<Symbol> calcDependencies(APersistentList<Node<? extends T>> nodes) {
		return calcDependencies(Vectors.coerce(nodes));
	}
	
	/**
	 * Gets the Magic Type for this expression
	 */
	public Type getType() {
		return Types.ANYTHING;
	}
	
	public SourceInfo getSourceInfo() {
		return source;
	}

	/**
	 * Evaluates the node in the given context, returning an updated context and value
	 * @param context
	 * @param bindings 
	 * @return
	 */
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		throw new UnsupportedOperationException("Cannot compile node of type: "+this.getClass());
	}

	/** 
	 * Specialises a node with a set of bindings
	 * 
	 * @param bindings
	 * @return
	 */
	public abstract Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings);
	
	/** 
	 * Performs local optimisations on the node. 
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
	 * Evaluates this Node in a quoted context, returning a form object
	 * 
	 * @param context
	 * @param bindings
	 * @return
	 */
	public abstract EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings, boolean syntaxQuote);
}
