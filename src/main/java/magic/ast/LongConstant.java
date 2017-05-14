package magic.ast;

import magic.RT;
import magic.compiler.SourceInfo;
import magic.data.Sets;

/**
 * AST node representing a constant long primitive value
 * 
 * TODO: figure out if primitives are best handles in a standard Constant node.
 * 
 * @author Mike
 *
 */
public class LongConstant extends BaseConstant<Long> {

	private final long value;

	public LongConstant(long value,SourceInfo source) {	
		super(Sets.emptySet(),source);
		this.value=value;
	}
	
	public static LongConstant create(long value,SourceInfo source) {	
		return new LongConstant(value,source);
	}
	
	public static LongConstant create(long value) {	
		return create(value,null);
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
