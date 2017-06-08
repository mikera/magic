package magic.data;

import java.util.List;
import static org.junit.Assert.assertEquals;

public class TestLists {
	
	public static <T> void testList(List<T> list) {
		testImmutableList(list);
	}
	
	public static <T> void testImmutableList(List<T> list) {
		testIterator(list);
	}

	public static <T> void testIterator(List<T> list) {
		int i=0;
		for (T t:list) {
			T gt=list.get(i++);
			assertEquals(gt,t);
		}
		assertEquals(list.size(),i);
	}
}
