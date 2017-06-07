package magic.lang;

import magic.Error;
import magic.data.Symbol;

public class UnresolvedException extends Error {
	private static final long serialVersionUID = -4696891540039509483L;

	private Symbol sym;

	public UnresolvedException(Symbol sym) {
		super("Unable to resolve symbol '"+sym+"'");
		this.sym=sym;
	}
	
	public Symbol getSymbol() {
		return sym;
	}

}
