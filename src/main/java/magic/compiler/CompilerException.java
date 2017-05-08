package magic.compiler;

public abstract class CompilerException extends Error {
	private static final long serialVersionUID = 2487799666843070700L;

	public CompilerException(String message) {
		super(message);
	}

}
