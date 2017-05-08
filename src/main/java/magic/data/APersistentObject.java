package magic.data;

import java.io.Serializable;

/**
 * Base for all magic persistent data classes
 * 
 * @author Mike Anderson
 *
 */
public abstract class APersistentObject implements IPersistentObject, Cloneable, Serializable {
	private static final long serialVersionUID = -4077880416849448410L;

	/**
	 * Clone returns the same object since PersistentObject
	 * and all subclasses must be immutable
	 * 
	 */
	@Override
	public APersistentObject clone() {
		return this;
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}
	
	@Override
	public void validate() {
		if (!this.clone().equals(this)) {
			throw new Error("Clone problem!");
		}
	}
}
