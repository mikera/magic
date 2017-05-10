package magic.data;

import magic.Type;

public interface IPersistentObject {

	public IPersistentObject clone();
	
	public boolean hasFastHashCode();
	
	/**
	 * Validates the persistent data structure
	 * 
	 * Should throw an exception in case of any problem
	 */
	public void validate();

	public Type getType();

}
