package magic.data;

import magic.Type;

/**
 * Class representing a keyword literal.
 * 
 * @author Mike
 *
 */
public class Keyword extends APersistentObject {
	private static final long serialVersionUID = -6288327152596122774L;

	private Symbol sym;
	private int hash;
	
	private Keyword(Symbol sym) {
		this.sym=sym;
		hash=sym.hashCode();
	}
	
	public static Keyword create(String name) {
		return create(Symbol.create(name));
	}
	
	public static Keyword create(magic.data.Symbol sym) {
		return new Keyword(sym);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Keyword)) return false;
		return equals((Keyword)o);
	}
	
	public boolean equals(Keyword k) {
		if (k==null) return false;
		if (k==this) return true;
		if (k.hash!=this.hash) return false;
		return sym.equals(k.sym);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return ":"+sym;
	}
	
	@Override
	public Keyword clone() {
		return this;
	}
	
	@Override
	public boolean hasFastHashCode() {
		return true;
	}

	@Override
	public void validate() {
		sym.validate();
	}

	@Override
	public Type getType() {
		return magic.type.Keyword.INSTANCE;
	}


}
