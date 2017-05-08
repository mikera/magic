package magic.data;

/**
 * Class representing a keyword literal.
 * 
 * @author Mike
 *
 */
public class Keyword {
	private Symbol sym;
	private int hash;
	
	private Keyword(Symbol sym) {
		this.sym=sym;
		hash=sym.hashCode();
	}
	
	public static Keyword create(magic.data.Symbol sym) {
		
		return new Keyword(sym);
	}
	
	public boolean equals(Keyword k) {
		if (k==this) return true;
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
}
