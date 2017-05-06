package magic.ast;

import magic.RT;
import magic.data.PersistentHashMap;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;

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
	public Long compute(Context c,PersistentHashMap<Symbol,?> bindings) {
		return value;
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
