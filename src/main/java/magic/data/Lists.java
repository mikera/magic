package magic.data;

public class Lists {

	public static final APersistentList<?> EMPTY=PersistentList.EMPTY;
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> emptyList() {
		return (APersistentList<T>) EMPTY;
	}

}
