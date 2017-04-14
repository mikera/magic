package magic.data;

public interface ISeq<T> {

	/**
	 * Returns the first item in a non-empty sequence
	 * @return
	 */
	public T first();
	
	/**
	 * Returns the remainder of this non-empty sequence, excluding the first element
	 * Returns null if there are no more elements.
	 * @return
	 */
	public ISeq<T> next();

}
