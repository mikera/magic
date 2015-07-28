package magic.fn;

public class ArityException extends RuntimeException {
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
