package magic.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSets {
	@Test public void testExclude() {
		assertEquals(Sets.of(1,2),Sets.of(1,2,3).exclude((Integer)3));
		assertEquals(Sets.of(1,2),Sets.of(0,1,2).exclude((Integer)0));
		assertEquals(Sets.of(1,2,4,5),Sets.of(1,2,3,4,5).exclude((Integer)3));
	}
	
	@Test public void testExcludeAll() {
		assertEquals(Sets.of(1,2,3),Sets.of(1,2,3).excludeAll(Sets.emptySet()));

		IPersistentSet<Integer> s1=Sets.of(1,2,3,4);
		IPersistentSet<Integer> s2=Sets.of(1,4,5);
		IPersistentSet<Integer> t=Sets.of(2,3);
		IPersistentSet<Integer> r=s1.excludeAll(s2);
		
		assertEquals(t,r);
	}
}
