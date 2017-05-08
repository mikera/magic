package magic.data;

import java.util.Collection;

/**
 * Interface for all persistent list types
 * @author Mike
 *
 * @param <T>
 */
public interface IPersistentList<T> extends IPersistentCollection<T>, Comparable<APersistentList<T>> {
	
	// modifier methods
	
	/**
	 * Concatenates a list to this list
	 */
	public APersistentList<T> concat(IPersistentList<T> values);

	/**
	 * Concatenates a values from a collection to this list
	 */
	public APersistentList<T> concat(Collection<T> values);

	public APersistentList<T> insert(int index, T value);
	
	public APersistentList<T> insertAll(int index, Collection<T> values);

	public APersistentList<T> insertAll(int index, IPersistentList<T> values);

	public APersistentList<T> copyFrom(int index, IPersistentList<T> values, int srcIndex, int length);
	
	@Override
	public APersistentList<T> include(T value);

	public APersistentList<T> deleteAt(int index);

	public APersistentList<T> deleteRange(int startIndex, int endIndex);

	public APersistentList<T> update(int index, T value);
	
	// query methods
	
	/**
	 * Returns the first element in the list
	 */
	public T head();
	
	/**
	 * Returns the tail of the list
	 * i.e. every element except the first
	 * @return
	 */
	public APersistentList<T> tail();
	
	/**
	 * Returns the front part of the list. Not guaranteed to be exactly half,
	 * but intended to be as balanced as possible
	 * @return
	 */
	public APersistentList<T> front();

	/**
	 * Returns the back part of the list. Not guaranteed to be exactly half,
	 * but intended to be as balanced as possible
	 * @return
	 */
	public APersistentList<T> back();
	

	public T get(int i);

	APersistentList<T> concat(APersistentList<T> values);
	
}
