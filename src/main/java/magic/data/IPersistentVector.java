package magic.data;

import java.util.Collection;
import java.util.List;

public interface IPersistentVector<T> extends IPersistentCollection<T>, List<T> {

	// modifier methods
	
	/**
	 * Concatenates a collection to this list
	 */
	public APersistentVector<T> concat(IPersistentCollection<? extends T> values);

	/**
	 * Concatenates a values from a collection to this list
	 */
	public APersistentVector<T> concat(Collection<? extends T> values);

	public APersistentVector<T> insert(int index, T value);
	
	public APersistentVector<T> insertAll(int index, Collection<? extends T> values);

	public APersistentVector<T> insertAll(int index, APersistentVector<? extends T> values);

	public APersistentVector<T> copyFrom(int index, IPersistentCollection<? extends T> values, int srcIndex, int length);
	
	@Override
	public APersistentVector<T> include(T value);

	public APersistentVector<T> deleteAt(int index);

	public APersistentVector<T> deleteRange(int startIndex, int endIndex);

	public APersistentVector<T> assocAt(int index, T value);
	
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
	public APersistentVector<T> tail();
	
	/**
	 * Returns the front part of the list. Not guaranteed to be exactly half,
	 * but intended to be as balanced as possible
	 * @return
	 */
	public APersistentVector<T> front();

	/**
	 * Returns the back part of the list. Not guaranteed to be exactly half,
	 * but intended to be as balanced as possible
	 * @return
	 */
	public APersistentVector<T> back();
	
	/**
	 * Returns a subset of the given list
	 * Can be the whole list, or an empty list
	 */
	@Override
	public APersistentVector<T> subList(int fromIndex, int toIndex);

	// access methods
	
	@Override
	public T get(int i);
}
