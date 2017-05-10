package magic.lang;

import magic.compiler.CompilerException;
import magic.data.Symbol;

public class UnresolvedException extends CompilerException {
	private static final long serialVersionUID = -4696891540039509483L;

	private Symbol sym;

	public UnresolvedException(Symbol sym) {
		super("Can't resolve symbol in current context: "+sym);
		this.sym=sym;
	}
	
	public Symbol getSymbol() {
		return sym;
	}

}
