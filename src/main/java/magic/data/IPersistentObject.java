package magic.data;

public interface IPersistentObject {

	public IPersistentObject clone();
	
	public boolean hasFastHashCode();
	
	/**
	 * Validates the persistent data structure
	 * 
	 * Should throw an exception in case of any problem
	 */
	public void validate();

}
