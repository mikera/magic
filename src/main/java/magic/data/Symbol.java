package magic.data;

import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import magic.RT;

/**
 * Class to represent a Magic symbol with optional namespace
 * 
 * @author Mike
 *
 */
public class Symbol {
	private final String ns;
	private final String name;
	private final int hash;

	private static final WeakHashMap<Symbol,WeakReference<Symbol>> symbols=new WeakHashMap<>();
	
	private Symbol(String ns, String name) {
		this.ns = ns;
		this.name = name;
		hash=RT.hashCombine(ns.hashCode(),name.hashCode());
	}
	
	private Symbol(String name) {
		this(null,name);
	}
	
	public static Symbol create(String ns, String name) {
		Symbol s=new Symbol(ns,name);
		WeakReference<Symbol> found=symbols.get(s);
		if (found==null) {
			return intern(s);
		} else {
			Symbol foundSym=found.get();
			if (foundSym!=null) return foundSym;
			return intern(s);
		}
	}
	
	public synchronized static Symbol intern(Symbol s) {
		WeakReference<Symbol> found=symbols.get(s);
		if (found!=null) {
			Symbol foundSym=found.get();
			if (foundSym!=null) return foundSym;
		}
		symbols.put(s, new WeakReference<Symbol>(s));
		return s;
	}
	
	public static Symbol create(String name) {
		return create(null,name);
	}
	
	private Object readResolve() throws ObjectStreamException {
		return create(ns, name);
	}

	public String getNamespace() {
		return ns;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Symbol) return equals((Symbol)o);
		return false;
	}
	
	public boolean equals(Symbol s) {
		if (s==this) return true;
		if (!RT.equals(name, s.name)) return false;
		if (!RT.equals(ns, s.ns)) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

}
