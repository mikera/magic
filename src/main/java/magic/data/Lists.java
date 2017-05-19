package magic.data;

public class Lists {

	public static final APersistentList<?> EMPTY=PersistentList.EMPTY;
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> emptyList() {
		return (APersistentList<T>) EMPTY;
	}

	public static <T> APersistentList<T> create(IPersistentCollection<T> a) {
		return PersistentList.create(Vectors.coerce(a));
	}

	public static <T> APersistentList<T> coerce(APersistentVector<T> vs) {
		return PersistentList.wrap(vs);
	}

	public static <T> APersistentList<T> cons(T value, APersistentList<T> rest) {
		return rest.include(value);
	}

}
