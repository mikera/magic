package magic.ast;

import magic.RT;
import magic.data.Sets;

public class LongConstant extends BaseConstant<Long> {

	private final long value;

	public LongConstant(long value) {	
		super(Sets.emptySet());
		this.value=value;
	}
	
	public static LongConstant create(long value) {	
		return new LongConstant(value);
	}

	@Override
	public Long getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "(LongConstant "+RT.print(value)+")";
	}


}
