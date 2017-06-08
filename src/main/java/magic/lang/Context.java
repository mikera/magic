package magic.lang;

import magic.RT;
import magic.ast.Node;
import magic.compiler.Reader;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Maps;
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

	private PersistentHashMap<Symbol,Slot<?>> mappings;
	
	/**
	 * The dependencies for each slot
	 */
	private final PersistentHashMap<Symbol,APersistentSet<Symbol>> dependants;
	
	private Context(PersistentHashMap<Symbol, Slot<?>> mappings, PersistentHashMap<Symbol,APersistentSet<Symbol>> deps) {
		this.mappings=mappings;
		this.dependants=deps;
	}
	
	/**
	 * Gets the value from the Slot associated with the given symbol in this context.
	 * May throw an Exception if the dependencies of the defined symbol are not available.
	 * @param sym
	 * @return
	 * @throws UnresolvedException if the symbol or one of it's dependencies is not defined.
	 */
	public <T> T getValue(Symbol sym) {
		return RT.resolve(this,sym);
	}
	
	/**
	 * Gets the value from the Slot associated with the symbol of the given name in this context.
	 * May throw an Exception if the dependencies of the defined symbol are not available.
	 * @param sym A string identifying a Symbol
	 * @return
	 * @throws UnresolvedException if the symbol or one of it's dependencies is not defined.
	 */
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
	
	@SuppressWarnings("unchecked")
	public <T> Context define(Symbol sym, Node<T> exp) {
		return define(sym,exp,(APersistentMap<Symbol, Object>)Maps.EMPTY);
    }
	 
	/**
	 * Defines a symbol in this context.
	 * 
	 * Causes an update to all dependencies as necessary, but does *not* evaluate dependencies.
	 * This enables contexts to be incrementally constructed without all dependencies yet available.
	 * 
	 * @param sym Symbol to define
	 * @param exp A Node definition for the raw, unexpanded code
	 * @param bindings Captured lexical bindings at the moment of definition
	 * @return
	 */
	public <T> Context define(Symbol sym, Node<T> exp, APersistentMap<Symbol, Object> bindings) {
		PersistentHashMap<Symbol,APersistentSet<Symbol>> newDependants=dependants;
		
		// remove old dependencies
		Slot<?> oldSlot=getSlot(sym);
		if (oldSlot!=null) {
			APersistentSet<Symbol> oldDeps=oldSlot.getDependencies();
			for (Symbol rsym: oldDeps) {
				newDependants=newDependants.assoc(rsym, newDependants.get(rsym).exclude(sym));
			}
		}
				
		// create the new Slot with this as the defining context
		Slot<T> newSlot=Slot.create(exp,this,bindings);
		
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
		
		// construct new Context with consistent dependencies
		PersistentHashMap<Symbol, Slot<?>> newMappings=mappings.assoc(sym,newSlot);
		Context c=new Context(newMappings,newDependants);
		
		// invalidate slots for transitive dependants if they exist
		// note we need to mutate mappings in place because circular reference is required
		// TODO: figure out what happens if dependency graph is affected?
		APersistentSet<Symbol> allDependants=calcTransitiveDependants(sym,newDependants);
		if (allDependants.size()>0) {
			for (Symbol s: allDependants) {
				Slot<?> slot=c.getSlot(s); // should not be null since it must have been defined in order to have an entry in the dependants graph?
				c.mappings=c.mappings.assoc(s, slot.invalidate(c));
			}
		}
		
		return c;
	}
	
	public APersistentSet<Symbol> calcDependants(Symbol sym) {
		return calcTransitiveDependants(sym,dependants);
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
	 * Gets a slot from the context. Returns null if the slot does not exist
	 * @param sym
	 * @return
	 */
	public Slot<?> getSlot(String symName) {
		return getSlot(Reader.readSymbol(symName));
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
