package magic.compiler;

import java.util.Map.Entry;

import magic.RT;
import magic.ast.Constant;
import magic.ast.Node;
import magic.data.APersistentMap;
import magic.data.Maps;
import magic.data.Symbol;
import magic.lang.Context;
import magic.type.JavaType;

public class AnalysisContext {

	private final Context context;
	private final APersistentMap<Symbol, Node<?>> bindings;

	public AnalysisContext(Context context, APersistentMap<Symbol, Node<?>> bindings) {
		this.context=context;
		this.bindings=bindings;
	}
	
	private AnalysisContext withBindings(APersistentMap<Symbol, Node<?>> newBindings) {
		return new AnalysisContext(context,newBindings);
	}

	public static AnalysisContext create(Context context) {
		return new AnalysisContext(context,Maps.empty());
	}
	
	public static AnalysisContext create(Context context,APersistentMap<Symbol, Object> bindings) {
		APersistentMap<Symbol, Node<?>> boundNodes=Maps.empty();
		for (Entry<Symbol, Object> e:bindings.entrySet()) {
			boundNodes=boundNodes.assoc(e.getKey(), Constant.create(e.getValue()));
		}
		return new AnalysisContext(context,boundNodes);
	}
	
	public AnalysisContext bind(Symbol sym,Node<?> node) {
		return withBindings(bindings.assoc(sym, node));
	}
	
	/**
	 * Attempts to resolve the given symbol in the analysis context
	 * @param sym
	 * @return
	 */
	public Symbol resolveSym(Symbol sym) {
		// OK if a local binding
		if (bindings.get(sym)!=null) return sym;

		return RT.resolveSym(context, sym);
	}

	/**
	 * Gets the node associated with a symbol in the current analysis context.
	 * Does *not* attempt to resolve symbols.
	 * 
	 * Could be:
	 * a) A local expression bindings
	 * b) A definition in the underlying context
	 * c) A host type
	 * 
	 * Returns null if no definition can be found
	 * 
	 * @param sym
	 * @return
	 */
	public Node<?> getNode(Symbol sym) {
		// handle a local binding
		Node<?> bound=bindings.get(sym);
		if (bound!=null) return bound;
		
		// handle a context reference in underlying context
		Node<?> node= context.getNode(sym);
		if (node!=null) return node;
		
		// handle a class lookup
		Class<?> klass=RT.classForSymbol(sym);
		if (klass!=null) return Constant.create(JavaType.create(klass));
		
		return null;
	}



}
