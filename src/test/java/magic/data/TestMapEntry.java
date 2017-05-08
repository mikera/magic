package magic.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestMapEntry {
	
	@Test public void testMapEntryInclude() {	
		MapEntry<Integer,Integer> me=MapEntry.create(1, 2);
		APersistentVector<Object> v=me.include(3);
		assertEquals(Tuple.of(1,2,3),v);
	}
	
	@Test public void testMapEntryExclude() {	
		MapEntry<Integer,Integer> me=MapEntry.create(1, 2);
		assertEquals(Tuple.of(1),me.exclude(2));
		assertEquals(Tuple.of(),me.excludeAll(Tuple.of(2,1)));
	}

}