package magic.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import mikera.util.Rand;

import org.junit.Test;

import magic.RT;
import magic.data.impl.NullMap;
import magic.data.impl.PersistentHashMap;

public class TestPersistentMap {
	@Test public void testBitMapFunctions() {
		assertEquals(2,PersistentHashMap.PHMBitMapNode.indexFromSlot(8, 0x00001111));
		assertEquals(0,PersistentHashMap.PHMBitMapNode.indexFromSlot(8, 0x00001100));
		
		assertEquals(3,PersistentHashMap.PHMBitMapNode.slotFromHash(0x00170030,4));
		assertEquals(1,PersistentHashMap.PHMBitMapNode.slotFromHash(0x00170030,20));
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testMaps() {
		PersistentMap<Integer,String> pm=PersistentHashMap.create();
		testMap(pm);
		testMap(addRandomMaps(pm));
		
		PersistentMap<Integer,String> nm=(PersistentMap<Integer,String>)NullMap.INSTANCE;
		testMap(nm);
		testMap(addRandomMaps(nm));
	}
	
	private PersistentMap<Integer, String> addRandomMaps(PersistentMap<Integer,String> im) {
		for (int i=0; i<Rand.d(50); i++) {
			im=im.assoc(Rand.r(50),Rand.nextString());
		}
		return im;
	}

	@Test public void testConvert() {
		PersistentMap<Integer,String> phm=PersistentHashMap.create();

		HashMap<Integer,String> hm=new HashMap<Integer, String>();
		for (int i=0; i<10; i++) {
			int key=Rand.r(100);
			String value=Rand.nextString();
			hm.put(key, value);
			phm=phm.assoc(key,value);
			
			int delKey=Rand.r(100);
			hm.remove(delKey);
			phm=phm.dissoc(delKey);
		}
		testMap(phm);
		
		PersistentMap<Integer,String> pm=MapFactory.create(hm);
		testMap(pm);
		
		HashMap<Integer,String> hm2=pm.toHashMap();
		assertEquals(hm,hm2);
		
		PersistentSet<Integer> ks=SetFactory.createFrom(hm.keySet());
		PersistentSet<Integer> ks2=pm.keySet();
		PersistentSet<Integer> ks3=phm.keySet();
		assertEquals(ks,ks2);
		assertEquals(ks,ks3);
		
		PersistentList<String> vs=ListFactory.createFromCollection(hm.values());
		PersistentList<String> vs2=ListFactory.createFromCollection(pm.values());
		PersistentList<String> vs3=ListFactory.createFromCollection(phm.values());
		assertEquals(SetFactory.createFrom(vs),SetFactory.createFrom(vs2));
		assertEquals(SetFactory.createFrom(vs),SetFactory.createFrom(vs3));
	}
	
	@Test public void testMerge() {
		PersistentMap<Integer,String> pm=PersistentHashMap.create();
		pm=pm.assoc(1, "Hello");
		pm=pm.assoc(2, "World");
		
		PersistentMap<Integer,String> pm2=PersistentHashMap.create();
		pm2=pm2.assoc(2, "My");
		pm2=pm2.assoc(3, "Good");
		pm2=pm2.assoc(4, "Friend");

		PersistentMap<Integer,String> mm=pm.include(pm2);
		assertEquals(4,mm.size());
	}
	
	@Test public void testToString() {
		HashMap<Integer,String> hm=new HashMap<Integer, String>();
		hm.put(1, "Hello");
		hm.put(2, "World");
		
		PersistentMap<Integer,String> pm=PersistentHashMap.create(1,"Hello");
		pm=pm.assoc(2,"World");
		assertEquals(PersistentHashMap.create(hm).toString(),pm.toString());
		assertEquals("{1=Hello, 2=World}",pm.toString());
	}
	
	@Test public void testChanges() {
		PersistentMap<Integer,String> pm=PersistentHashMap.create();
		pm=pm.assoc(1, "Hello");
		pm=pm.assoc(2, "World");
		
		assertEquals(null,pm.get(3));
		assertEquals("Hello",pm.get(1));
		assertEquals("World",pm.get(2));
		assertEquals(2,pm.size());
		
		pm.validate();
		pm=pm.assoc(2, "Sonia");
		pm.validate();
		assertEquals("Hello",pm.get(1));
		assertEquals("Sonia",pm.get(2));
		assertEquals(2,pm.size());

		pm=pm.dissoc(1);
		assertEquals(null,pm.get(1));
		assertEquals("Sonia",pm.get(2));
		assertEquals(1,pm.size());		
		
		assertTrue(pm.values().contains("Sonia"));
		assertTrue(pm.keySet().contains(2));
		
		testMap(pm);
	}
	
	public void testMap(PersistentMap<Integer,String> pm) {
		pm.validate();
		testIterator(pm);
		testRandomAdds(pm);
		testNullAdds(pm);
		testEquals(pm);
		CommonTests.testCommonData(pm);
	}
	
	public void testIterator(PersistentMap<Integer,String> pm) {
		int i=0;
		for (Map.Entry<Integer,String> ent: pm.entrySet()) {
			assertTrue(pm.containsKey(ent.getKey()));
			assertTrue(RT.equals(ent.getValue(), pm.get(ent.getKey())));
			i++;
		}
		assertEquals(pm.size(),i);
	}
	
	public void testRandomAdds(PersistentMap<Integer,String> pm) {
		pm=addRandomStuff(pm,100,1000000);
		int size=pm.size();
		assertTrue(size>90);
		assertEquals(size,pm.entrySet().size());
		assertEquals(size,pm.keySet().size());
		assertEquals(size,pm.values().size());	
	}
	
	public void testNullAdds(PersistentMap<Integer,String> pm) {
		pm=pm.assoc(2,null);	
		assertTrue(pm.containsKey(2));
		assertEquals(null,pm.get(2));	
	}
	
	public void testEquals(PersistentMap<Integer,String> pm) {
		PersistentMap<Integer,String> pm2=pm.assoc(2,new String("Hello"));
		PersistentMap<Integer,String> pm3=pm.assoc(2,new String("Hello"));
		assertEquals(pm2,pm3);
	}


	
	public PersistentMap<Integer,String> addRandomStuff(PersistentMap<Integer,String> pm, int n , int maxIndex ) {
		for (int i=0; i<n; i++) {
			pm=pm.assoc(Rand.r(maxIndex),Rand.nextString());
		}
		return pm;
	}
	
	@Test public void testManyChanges() {
		PersistentMap<Integer,String> pm=PersistentHashMap.create();
		pm=addRandomStuff(pm,1000,40);
		assertEquals(40,pm.size());
		testMap(pm);
	}

}