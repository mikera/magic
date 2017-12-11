package magic.data;

import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import magic.RT;
import magic.Type;
import magic.compiler.Reader;

/**
 * Class to represent a Magic symbol with optional namespace
 * 
 * @author Mike
 *
 */
public class Symbol extends APersistentObject {
	private static final long serialVersionUID = -3902663236353633212L;

	private final String ns;
	private final String name;
	private final int hash;
	private APersistentSet<Symbol> symbolSet=null;

	private static final WeakHashMap<Symbol,WeakReference<Symbol>> symbols=new WeakHashMap<>();
	
	private Symbol(String ns, String name) {
		// TODO: figure out if interning is a good idea or not
//		this.ns = ns.intern();
//		this.name = name.intern();
		this.ns = ns;
		this.name = name;
		hash=RT.hashCombine(RT.hashCode(ns),RT.hashCode(name));
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
	
	public static Symbol createCore(String name) {
		return create("magic.core",name);
	}
	
    /**
     * Like create, but different arg order to support parsing (i.e. ordering of pop()s)
     * @param name
     * @param nameSpace
     * @return
     */
	public static Symbol createWithNamespace(String name, String nameSpace) {
		return create(nameSpace,name);
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
		return Reader.readSymbol(name);
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
	
	@Override
	public String toString() {
		if (ns==null) return name;
		return ns+"/"+name;
	}

	/**
	 * Returns a set containing this symbol
	 * @return
	 */
	public APersistentSet<Symbol> symbolSet() {
		if (symbolSet==null) {
			APersistentSet<Symbol> result=Sets.of(this);
			symbolSet=result;
			return result;
		}
		return symbolSet;
	}

	@Override
	public Type getType() {
		return magic.type.Symbol.INSTANCE;
	}

	public boolean isQualified() {
		return ns!=null;
	}

	public static APersistentSet<Symbol> createSet(String...  strings) {
		APersistentSet<Symbol> ss=Sets.emptySet();
		for (String s: strings) {
			ss=ss.include(Symbol.create(s));
		}
		return ss;
	}


}
