package magic.lang;

import magic.Type;
import magic.Types;
import magic.ast.Node;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Maps;
import magic.data.Symbol;

/**
 * Represents a "slot" in a magic Context.
 * 
 * A slot features:
 * - An expression stored as a Node
 * - A lazily computed value
 * 
 * @author Mike
 *
 * @param T the Java type of the expression
 */
public class Slot<T> {
	private final Node<T> rawExpression;
	private final APersistentMap<Symbol, Object> bindings;
	private final Context context;
	
	private T value=null;
	private volatile boolean computed=false;
	private Node<T> compiledExpression=null;
	
	private Slot(Node<T> e, Context context, APersistentMap<Symbol, Object> bindings) {
		this.rawExpression=e;
		this.context=context;
		this.bindings=bindings;
	}
	
    /**
     * Gets the value associated with this Slot. 
     * 
     * Forces computation of the value if it has not already been computed
     * 
     * @return
     */
	public T getValue() {
		if (computed==false) {
			synchronized (this) {
				// double-checked locking, just in case.....
				if (computed==false) {
					return tryCompute();
				}
			}
		}
		return value;
	}
	
	private T tryCompute() {
//		APersistentSet<Symbol> deps=expression.getDependencies();
//		if (!deps.isEmpty()) {
//			// check slots exist
//			for (Symbol s:deps) {
//				if (c.getSlot(s)==null) throw new UnresolvedException(s);
//			}
//		}
		EvalResult<T> result=getNode().eval(context,bindings);
		value=result.getValue();
		computed=true;
		return value;
	}
	
	/**
	 * Gets the compiled Node associated with this Slot.
	 * Note that this Node may have unresolved dependencies.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Node<T> getNode() {
		if (compiledExpression==null) {
			compiledExpression=(Node<T>) magic.compiler.Compiler.compileNode(context,bindings,rawExpression);
		}
		return compiledExpression;
	}


	@SuppressWarnings("unchecked")
	public static <T> Slot<T> create(Node<T> exp,Context context) {
		return create(exp,context,(APersistentMap<Symbol, Object>)Maps.EMPTY);
	}

	public static <T> Slot<T> create(Node<T> exp, Context context,APersistentMap<Symbol, Object> bindings) {
		return new Slot<T>(exp,context,bindings);
	}

	public boolean isExpander() {
		return Types.EXPANDER.contains(getType());
	}
	
	public Type getType() {
		return getNode().getType();
	}
	
	public boolean isComputed() {
		return computed;
	}

	public APersistentSet<Symbol> getDependencies() {
		APersistentSet<Symbol> rawDeps= getNode().getDependencies();
		return rawDeps;
	}

	/**
	 * Invalidates the slot, returning a slot with no cached values.
	 * 
	 * Associates the given defining context with the slot, which will be used to 
	 * compute the slot's value if it is subsequently requested.
	 * 
	 * @return
	 */
	public Slot<T> invalidate(Context c) {
		if ((!computed)&&(c==context)) return this;
		return create(rawExpression,c,bindings);
	}
	
	@Override 
	public String toString() {
		return "<Slot exp="+rawExpression+(computed?(" val="+value):"")+">";
	}

}
