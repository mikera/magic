package magic.lang;

import magic.data.PersistentHashMap;
import magic.data.Symbol;
import magic.expression.Expression;

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
	
	public <T> Context define(Symbol sym, Expression<T> exp) {
		return create(mappings.assoc(sym,Slot.create(exp)));
	}

	private static Context create(PersistentHashMap<Symbol, Slot<?>> mappings) {
		return new Context(mappings);
	}

	@SuppressWarnings("unchecked")
	public <T> Slot<T> getSlot(Symbol sym) {
		return (Slot<T>) mappings.get(sym);
	}

}
