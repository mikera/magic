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
 * - An definition stored as a Node
 * - A lazily computed value
 * 
 * @author Mike
 *
 * @param T the Java type of the expression
 */
public class Slot<T> {
	private final APersistentMap<Symbol, Object> bindings;
	private Context context;
	
	private T value=null;
	private volatile boolean computed=false;

	private final Node<T> rawExpression;
	private Node<T> expandedExpression=null;
	private Node<T> compiledExpression=null;
	private int expansionCount; // for detecting recursive expansion
	
	private Slot(Node<T> e, Context context, APersistentMap<Symbol, Object> bindings) {
		this.rawExpression=e;
		this.context=context;
		this.bindings=bindings;
	}
	
    /**
     * Gets the value associated with this Slot. 
     * 
     * Forces computation of the value if it has not already been computed.
     * Computation occurs in the context in which the slot was defined. As contexts are 
     * immutable, this is guaranteed to be stable.
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
		EvalResult<T> result=getCompiledNode().eval(context,bindings);
		value=result.getValue();
		computed=true;
		return value;
	}
	
	/**
	 * Gets the compiled Node associated with this Slot. Will compile the node if not already compiled.
	 * 
	 * Note that this Node may have unresolved dependencies.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Node<T> getCompiledNode() {
		if (compiledExpression==null) {
			compiledExpression=(Node<T>) magic.compiler.Compiler.compileNode(context,bindings,getExpandedNode());
		}
		return compiledExpression;
	}

	/**
	 * Gets the expanded Node associated with this Slot. 
	 * 
	 * Note that this Node may have unresolved dependencies.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private synchronized Node<T> getExpandedNode() {
		if (expansionCount>0) throw new Error("Recursive expansion while expanding slot definition: " + rawExpression);
		if (expandedExpression==null) {
			// analysedExpression=rawExpression;
			try {
				expansionCount+=1;
				expandedExpression=(Node<T>) magic.compiler.Compiler.expand(context,rawExpression);
			} catch (StackOverflowError t) {
				throw new Error("Infinite expansion while expanding slot definition: " + rawExpression,t); 			
			} finally {
				expansionCount-=1;
			}
		}
		return expandedExpression;
	}

	@SuppressWarnings("unchecked")
	public static <T> Slot<T> create(Node<T> exp,Context context) {
		return create(exp,context,(APersistentMap<Symbol, Object>)Maps.EMPTY);
	}

	public static <T> Slot<T> create(Node<T> exp, Context context,APersistentMap<Symbol, Object> bindings) {
		return new Slot<T>(exp,context,bindings);
	}

	public boolean isExpander() {
		// TODO: better identification of expanders?
		if (expansionCount>0) return false;
		return Types.EXPANDER.contains(getType());
	}
	
	public Type getType() {
		// TODO: figure out how to avoid recursive compilation
		//if (expansionCount>0) return rawExpression.getType();

		if (compiledExpression==null) {
			return getExpandedNode().getType();
		} else {
			return compiledExpression.getType();
		}
	}
	
	public boolean isComputed() {
		return computed;
	}

	/**
	 * Gets the symbolic dependencies for this slot.
	 * 
	 * Requires the node to be analysed, forces analysis if not yet done. Dependencies may not yet exist.
	 * 
	 * @return
	 */
	public APersistentSet<Symbol> getDependencies() {
		APersistentSet<Symbol> deps= getCompiledNode().getDependencies();
		return deps;
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
		return create(rawExpression,c,bindings);
	}
	
	@Override 
	public String toString() {
		return "<Slot raw="+rawExpression+(computed?("\n      val="+value):"")+">";
	}

	/** 
	 * Change the context to allow for circular references to this slot. Internal use only!
	 * @param c
	 */
	void hackContext(Context c) {
		context =c;
	}

}
