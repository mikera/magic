package magic.expression;

import com.oracle.truffle.api.frame.VirtualFrame;

import magic.lang.Context;

public class IntConstant extends BaseConstant<Number> {

	private final long value;

	public IntConstant(long value) {
		
		this.value=value;
	}
	

	@Override
	public Number compute(Context c) {
		// TODO Auto-generated method stub
		return value;
	}

}
