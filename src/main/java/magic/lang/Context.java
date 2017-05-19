package magic.lang;

import magic.ast.Node;
import magic.compiler.Reader;
import magic.data.PersistentHashMap;
import magic.data.Symbol;

/**
 * Class representing an Execution context.
 * 
 * Manages a Map of Symbol->Slot
 * @author Mike
 *
 */
public class Context {
	@SuppressWarnings("unchecked")
	public static final Context EMPTY=new Context((PersistentHashMap<Symbol, Slot<?>>) PersistentHashMap.EMPTY);

	private final PersistentHashMap<Symbol,Slot<?>> mappings;
	
	private Context(PersistentHashMap<Symbol, Slot<?>> mappings) {
		this.mappings=mappings;
	}
	
	public <T> T getValue(Symbol sym) {
		Slot<T> slot=getSlot(sym); 
		if (slot==null) throw new IllegalArgumentException("Symbol not defined: "+sym);
		return slot.getValue(this);
	}
	
	public <T> Node<T> getExpression(Symbol sym) {
		Slot<T> slot=getSlot(sym); 
		if (slot==null) throw new IllegalArgumentException("Symbol not defined: "+sym);
		return slot.getExpression();
	}
	
	public <T> T getValue(String sym) {
		return getValue(Reader.readSymbol(sym));
	}
	
	// TODO: need to attach source?
	public <T> Context define(Symbol sym, Node<T> exp) {
		return create(mappings.assoc(sym,Slot.create(exp)));
	}

	private static Context create(PersistentHashMap<Symbol, Slot<?>> mappings) {
		return new Context(mappings);
	}
	
	public static <T> Context createWith(Symbol sym,Node<T> e) {
		return EMPTY.define(sym, e);
	}
	
	public static <T> Context createWith(String sym,Node<T> e) {
		return createWith(Reader.readSymbol(sym),e);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Gets a slot from the context. Returns null if the slot does not exist
	 * @param sym
	 * @return
	 */
	public <T> Slot<T> getSlot(Symbol sym) {
		return (Slot<T>) mappings.get(sym);
	}

	public Node<?> getExpression(String symbol) {
		return getExpression(Reader.readSymbol(symbol));
	}

	public String getCurrentNamespace() {
		return getValue(Symbols._NS_);
	}




}
