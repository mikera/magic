package magic.ast;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import magic.data.IPersistentSet;
import magic.data.IPersistentVector;
import magic.data.PersistentHashMap;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.MagicLanguage;
 
/**
 * Base class for Expressions
 * @author Mike
 *
 */
@NodeInfo(language = "Magic", description = "The abstract base node for all expressions")
public abstract class Node<T> extends RootNode {

	private IPersistentSet<Symbol> deps;

	public Node(IPersistentSet<Symbol> deps) {
		this(MagicLanguage.class,null,null);
		this.deps=deps;
	}
	
	public Node(Class<? extends TruffleLanguage<?>> language, SourceSection sourceSection,
			FrameDescriptor frameDescriptor) {
		super(language, sourceSection, frameDescriptor);
	}

	@SuppressWarnings("unchecked")
	public final T compute(Context c) {
		return compute(c,(PersistentHashMap<Symbol, ?>) PersistentHashMap.EMPTY);
	}

	public abstract T compute(Context c, PersistentHashMap<Symbol,?> bindings);
	
	@Override
	public Object execute(VirtualFrame virtualFrame) {
		return compute(null);
	}
	
	public T getValue() {
		throw new UnsupportedOperationException("Cannont use getValue() on a non-constant expression");
	}
	
	public IPersistentSet<Symbol> getDependencies() {
		return deps;
	}
	
	protected static IPersistentSet<Symbol> calcDependencies(Node<?> f, Node<?>[] args) {
		IPersistentSet<Symbol> deps=f.getDependencies();
		deps=deps.includeAll(calcDependencies(args));
		return deps;
	}
	
	protected static IPersistentSet<Symbol> calcDependencies(Node<?>[] nodes) {
		IPersistentSet<Symbol> deps=Sets.emptySet();
		for (int i=0; i<nodes.length; i++) {
			deps=deps.includeAll(nodes[i].getDependencies());
		}
		return deps;
	}
	
	protected static <T> IPersistentSet<Symbol> calcDependencies(IPersistentVector<Node<T>> nodes) {
		IPersistentSet<Symbol> deps=Sets.emptySet();
		int n=nodes.size();
		for (int i=0; i<n; i++) {
			deps=deps.includeAll(nodes.get(i).getDependencies());
		}
		return deps;
	}
}
