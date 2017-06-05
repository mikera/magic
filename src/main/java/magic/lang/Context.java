package magic.lang;

import magic.ast.Node;
import magic.compiler.Reader;
import magic.data.APersistentSet;
import magic.data.PersistentHashMap;
import magic.data.Sets;
import magic.data.Symbol;

/**
 * Class representing an execution context.
 * 
 * Manages a Map of Symbol->Slot
 * @author Mike
 *
 */
public class Context {
	@SuppressWarnings("unchecked")
	public static final Context EMPTY=new Context(
			(PersistentHashMap<Symbol, Slot<?>>) PersistentHashMap.EMPTY,
			(PersistentHashMap<Symbol,APersistentSet<Symbol>>)PersistentHashMap.EMPTY);

	private final PersistentHashMap<Symbol,Slot<?>> mappings;
	
	/**
	 * The dependencies for each slot
	 */
	private final PersistentHashMap<Symbol,APersistentSet<Symbol>> dependants;
	
	private Context(PersistentHashMap<Symbol, Slot<?>> mappings, PersistentHashMap<Symbol,APersistentSet<Symbol>> deps) {
		this.mappings=mappings;
		this.dependants=deps;
	}
	
	public <T> T getValue(Symbol sym) {
		Slot<T> slot=getSlot(sym); 
		if (slot==null) throw new UnresolvedException(sym);
		return slot.getValue(this);
	}
	
	public <T> T getValue(String sym) {
		return getValue(Reader.readSymbol(sym));
	}
	
	/**
	 * Defines a symbol in the current context
	 * @param sym String containing the symbol to define
	 * @param exp A Node definition
	 * @return
	 */
	public <T> Context define(String sym, Node<T> exp) {
		Symbol s=Reader.readSymbol(sym);
		return define(s,exp);
	}
	
	/**
	 * Defines a symbol in the current context
	 * @param sym Symbol to define
	 * @param exp A Node definition for the raw, unexpanded code
	 * @return
	 */
	public <T> Context define(Symbol sym, Node<T> exp) {
		PersistentHashMap<Symbol,APersistentSet<Symbol>> newDependants=dependants;
		
		// remove old dependencies
		Slot<?> oldSlot=getSlot(sym);
		if (oldSlot!=null) {
			APersistentSet<Symbol> oldDeps=oldSlot.getDependencies();
			for (Symbol rsym: oldDeps) {
				newDependants=newDependants.assoc(rsym, newDependants.get(rsym).exclude(sym));
			}
		}
				
		// create the new Slot
		Slot<T> newSlot=Slot.create(exp);
		
		// include new dependencies
		APersistentSet<Symbol> dependencies=newSlot.getDependencies();
		for (Symbol nsym: dependencies) {
			APersistentSet<Symbol> t=newDependants.get(nsym);
			if (t==null) {
				t=Sets.of(sym);
			} else {
				t=t.include(sym);
			}
			newDependants=newDependants.assoc(nsym, t);
		}
		
		PersistentHashMap<Symbol, Slot<?>> newMappings=mappings.assoc(sym,newSlot);

		// invalidate transitive dependants
		APersistentSet<Symbol> allDependants=calcTransitiveDependants(sym,newDependants);
		if (allDependants.size()>0) {
			for (Symbol s: allDependants) {
				Slot<?> slot=getSlot(s);
				if (slot!=null) newMappings=newMappings.assoc(s, slot.invalidate());
			}
		}
		
		return new Context(newMappings,newDependants);
	}
	
	private static APersistentSet<Symbol> calcTransitiveDependants(Symbol sym, PersistentHashMap<Symbol, APersistentSet<Symbol>> dependants) {
		return calcTransitiveDependants(sym,dependants,Sets.emptySet());
	}
	
	private static APersistentSet<Symbol> calcTransitiveDependants(Symbol sym, PersistentHashMap<Symbol, APersistentSet<Symbol>> dependants, APersistentSet<Symbol> found) {
		APersistentSet<Symbol> syms=dependants.get(sym);
		if (syms==null) return found;
		for (Symbol s :syms) {
			if (!found.contains(s)) {
				found=found.include(s);
				found=calcTransitiveDependants(s,dependants,found);
			}
		}
		return found;
	}

	/**
	 * Gets the dependencies for a given symbol in this context
	 * @param sym
	 * @return
	 */
	public APersistentSet<Symbol> getDependencies(String sym) {
		return getDependencies(Reader.readSymbol(sym));
	}
	
	/**
	 * Gets the dependants for a given symbol in this context
	 * @param sym
	 * @return
	 */
	public APersistentSet<Symbol> getDependants(String sym) {
		return getDependants(Reader.readSymbol(sym));
	}
	
	/**
	 * Gets the dependencies for a given symbol in this context
	 * @param sym
	 * @return
	 */
	public APersistentSet<Symbol> getDependencies(Symbol sym) {
		return getSlot(sym).getDependencies();
	}
	
	/**
	 * Gets the dependants for a given symbol in this context
	 * @param sym
	 * @return
	 */
	public APersistentSet<Symbol> getDependants(Symbol sym) {
		APersistentSet<Symbol> ds=dependants.get(sym);
		return (ds==null)?Sets.emptySet():ds;
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

	/**
	 * Gets the Node in this context associated with a given symbol name
	 * @param symbol
	 * @return
	 */
	public Node<?> getNode(String symbol) {
		return getNode(Reader.readSymbol(symbol));
	}
	
	/**
	 * Gets the Node in this context associated with a given symbol name
	 * @param symbol
	 * @return
	 */
	public <T> Node<T> getNode(Symbol sym) {
		Slot<T> slot=getSlot(sym); 
		if (slot==null) throw new IllegalArgumentException("Symbol not defined: "+sym);
		return slot.getNode();
	}
	

	public String getCurrentNamespace() {
		return getValue(Symbols._NS_);
	}




}
