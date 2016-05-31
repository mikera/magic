package magic.fn;

public class ArityException extends RuntimeException {
	private static final long serialVersionUID = 7873755099725178754L;

	public ArityException(String s) {
		super(s);
	}

	public ArityException(int arity) {
		this("Arity not defined: "+arity);
	}
	
	public ArityException(int expected , int arity) {
		this("Expected arity "+expected+" but called with: "+arity);
	}
}
