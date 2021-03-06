package magic.compiler;

import magic.Error;

public class AnalyserException extends Error {

	private static final long serialVersionUID = 5766500154253913151L;
	private Object form;

	public AnalyserException(String message, Object form) {
		super(message);
		this.form=form;
	}
	
	public Object getForm() {
		return form;
	}
	
	@Override
	public String toString() {
		return getMessage()+ "\n"
			+"while analysing: "+form;
	}

}
