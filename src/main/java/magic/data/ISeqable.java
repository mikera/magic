package magic.data;

/**
 * Interface for Objects that support seq()
 * 
 * 
 * @author Mike
 *
 * @param <T>
 */
public interface ISeqable<T> {

	/**
	 * Returns the values in this collection as a seq, or null if empty
	 */
	public ISeq<T> seq();
}
