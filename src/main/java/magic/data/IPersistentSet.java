package magic.data;

import java.util.Collection;
import java.util.Set;

public interface IPersistentSet<T> extends Set<T>, Iterable<T> {
	// delete methods
	
	public IPersistentSet<T> delete(final T value);
	
	public IPersistentSet<T> deleteAll(final Collection<T> values);

	public IPersistentSet<T> deleteAll(final APersistentCollection<T> values);

	// include methods
	
	public IPersistentSet<T> includeAll(final Collection<T> values);

	public IPersistentSet<T> includeAll(final IPersistentSet<T> values);

	// query methods
	
	public boolean allowsNulls();
}
