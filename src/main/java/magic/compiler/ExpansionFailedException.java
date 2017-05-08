package magic.compiler;

public class ExpansionFailedException extends CompilerException {

	private static final long serialVersionUID = 5766500154253913151L;
	private Object form;

	public ExpansionFailedException(String message, Object form) {
		super(message);
		this.form=form;
	}
	
	public Object getForm() {
		return form;
	}
	
	@Override
	public String toString() {
		return getMessage()+ " while expanding: "+form;
	}

}