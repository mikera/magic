package magic.type;

import magic.Type;

/**
 * Abstract base class for types that combine other types.
 * 
 * @author Mike
 *
 */
public abstract class ACompoundType extends Type {
	protected final Type[] types;
	
	protected ACompoundType(Type[] types) {
		this.types=types;
	}
	
}
