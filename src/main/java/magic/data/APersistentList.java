package magic.data;

/**
 * Abstract base class for persistent lists
 * @author Mike
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class APersistentList<T> extends APersistentCollection<T> implements IPersistentList<T> {
   
	@Override
	public APersistentList<T> empty() {
		return Lists.emptyList();
	}

	@Override
	public int compareTo(APersistentList<T> o) {
		// TODO: optimise
		return Vectors.coerce(this).compareTo(Vectors.coerce(o));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof APersistentList<?>) {
			return equals((APersistentList<?>)o);
		}
		return false;
	}
	
	public boolean equals(APersistentList<?> a) {
		// TODO: optimise
		return (Vectors.coerce(this)).equals(Vectors.coerce(a));
	}
}
