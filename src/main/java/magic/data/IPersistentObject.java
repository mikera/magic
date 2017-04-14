package magic.data;

import java.io.Serializable;

public interface IPersistentObject extends Cloneable, Serializable {

	public IPersistentObject clone();
	
	public boolean hasFastHashCode();
	
	/**
	 * Validates the persistent data structure
	 * 
	 * Should throw an exception in case of any problem
	 */
	public void validate();

}
