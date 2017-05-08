package magic.compiler;

import magic.data.IPersistentList;

public class ExpansionFailedException extends CompilerException {

	private static final long serialVersionUID = 5766500154253913151L;
	private IPersistentList<Object> form;

	public ExpansionFailedException(String message, IPersistentList<Object> form) {
		super(message);
		this.form=form;
	}
	
	public IPersistentList<Object> getForm() {
		return form;
	}

}
