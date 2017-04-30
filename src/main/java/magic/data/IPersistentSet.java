package magic.data;

import java.util.Collection;
import java.util.Set;

public interface IPersistentSet<T> extends Set<T>, Iterable<T> {
	// delete methods
	
	public IPersistentSet<T> exclude(final T value);
	
	public IPersistentSet<T> excludeAll(final Collection<T> values);

	public IPersistentSet<T> excludeAll(final APersistentCollection<T> values);

	// include methods
	
	public IPersistentSet<T> includeAll(final Collection<T> values);

	public IPersistentSet<T> includeAll(final IPersistentSet<T> values);

	// query methods
	
	public boolean allowsNulls();
}
