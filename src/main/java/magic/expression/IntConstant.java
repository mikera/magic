package magic.expression;

import magic.lang.Context;

public class IntConstant extends BaseConstant<Number> {

	private final long value;

	public IntConstant(long value) {	
		this.value=value;
	}
	
	@Override
	public Number compute(Context c) {
		return value;
	}

}
