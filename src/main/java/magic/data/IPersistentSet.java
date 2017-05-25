package magic.data;

import java.util.Collection;
import java.util.Set;

public interface IPersistentSet<T> extends Set<T>, Iterable<T> {
	// delete methods
	
	public IPersistentSet<T> exclude(final T value);
	
	public IPersistentSet<T> excludeAll(final Collection<? extends T> values);

	public IPersistentSet<T> excludeAll(final APersistentCollection<? extends T> values);

	// include methods
	
	public IPersistentSet<T> includeAll(final Collection<? extends T> values);

	public IPersistentSet<T> includeAll(final IPersistentSet<? extends T> values);

	// query methods
	
	public boolean allowsNulls();

	public IPersistentSet<T> excludeAll(T[] syms);
}
