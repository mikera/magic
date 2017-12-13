package magic.ast;

import magic.compiler.EvalResult;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node representing a compiler action on the current context
 * 
 * This may make arbitrary changes to the context
 * Probably should never be accessible from user code?
 * 
 * @author Mike
 *
 * @param <T>
 */
public class ContextAction<T> extends BaseForm<T> {

	private final Action<T> action;
	
	public static abstract class Action<T> {
		public abstract EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings,Object[] args);
	}

	
	public ContextAction(APersistentList<Node<?>> nodes, Action<T> aFn, APersistentMap<Keyword,Object> meta) {
		super(nodes,meta);
		this.action=aFn;
	}
	
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		int n=nodes.size()-1;
		Object[] args=new Object[n];
		for (int i=0; i<n; i++) {
			args[i]=nodes.get(i+1).eval(context,bindings).getValue();
		}
		return action.eval(context,bindings,args);
	}
	
	@Override
	public ContextAction<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentList<Node<?>> newExps=NodeFunctions.mapAll(nodes, fn);
		if (newExps==nodes) return this;
		return new ContextAction<T>(newExps,action,meta());
	}

	@Override
	public Node<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new ContextAction<T>(nodes,action,meta);
	}

	@Override
	public String toString() {
		return "(CONTEXT-ACTION!!)";
	}
	
}
