package magic.ast;

import magic.RT;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Maps;
import magic.lang.Keywords;

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

	public LongConstant(long value,APersistentMap<Keyword, Object> meta) {	
		super(meta);
		this.value=value;
	}
	
	@Override
	public Node<Long> withMeta(APersistentMap<Keyword, Object> meta) {
		return new LongConstant(value,meta);
	}


	
	public static LongConstant create(long value,SourceInfo source) {	
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.SOURCE,source);
		// meta=meta.assoc(Keywords.DEPS, Sets.emptySet());
		return new LongConstant(value,meta);
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
