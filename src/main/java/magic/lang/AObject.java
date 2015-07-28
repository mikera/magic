package magic.lang;

import java.io.Serializable;

/**
 * Abstract base class for Magic language objects
 * @author Mike
 *
 */
public abstract class AObject implements Cloneable, Serializable {
	
	public abstract long hash();

	public abstract long hash(long seed);
	
	public int hashCode() {
		long hash=hash();
		return ((int)hash)^((int)(hash>>>32));
	}
	
	public AObject clone() {
		try {
			return (AObject) super.clone();
		} catch (CloneNotSupportedException e) {
			Tools.sneakyThrow(e);
			return null;
		}		
	}
}
