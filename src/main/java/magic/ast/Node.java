package magic.ast;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

import magic.data.PersistentHashMap;
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

	public Node() {
		this(MagicLanguage.class,null,null);
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
}
