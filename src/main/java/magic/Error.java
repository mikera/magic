package magic;

public class Error extends java.lang.Error {
	private static final long serialVersionUID = 2487799666843070700L;

	public Error(String message) {
		super(message);
	}
	
	public Error(String message, Throwable cause) {
		super(message,cause);
	}

}
