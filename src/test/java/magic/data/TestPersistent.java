package magic.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import magic.RT;
import magic.data.impl.CompositeVector;
import magic.data.impl.NullCollection;
import magic.data.impl.EmptyVector;
import magic.data.impl.NullSet;
import magic.data.impl.RepeatVector;
import magic.data.impl.SingletonSet;
import magic.data.impl.SubVector;
import mikera.util.Rand;

public class TestPersistent {
	
	@Test public void testListTypes() {	
		APersistentVector<Integer> pl=Vectors.createFromArray(new Integer[] {1,2,3,4,5});
		assertEquals(5,pl.size());
		
		testPersistentList(pl);
		testPersistentList(pl.subList(1, 4));
		testPersistentList(EmptyVector.INSTANCE);
		testPersistentList(Tuple.of(1,2,3,4,5));
		testPersistentList(RepeatVector.create("Hello persistent lists!",1));
		testPersistentList(RepeatVector.create("Hello", 40));
		testPersistentList(MapEntry.create(Keyword.create("foo"), 40));
		testPersistentList(CompositeVector.create(pl));
		testPersistentList(PersistentVector.create(pl));
		testPersistentList(PersistentVector.create(RepeatVector.create("MM", 40)));
		testPersistentList(SubVector.create(pl,2,3));
	}
	
	@Test public void testCollectionTypes() {
		testPersistentCollection(NullCollection.INSTANCE);
		testPersistentCollection(PersistentHashMap.create().assoc(1, "Sonia").values());
	}
	
	@Test public void testUpdate() {
		assertEquals(Tuple.of(1,2,3),Tuple.of(1,2,4).update(2, 3));
	}
	
	@Test public void testConcat() {
		assertEquals(Tuple.of(1,2),Tuple.concat(Tuple.of(1), Tuple.of(2)));
		
		Tuple<Integer> ts=Tuple.of(0,1,2,3,4,5,6,7,8,9);
		assertEquals(ts,Tuple.concat(ts, ts).subList(10, 20));
		
		PersistentVector <Integer> bs=PersistentVector.create(ts);
		assertEquals(ts,bs);
		PersistentVector <Integer> bs2=bs.concat(bs);
		assertEquals(ts,bs2.subList(10, 20));
		bs2.validate();
		
		PersistentVector <Integer> bs3=bs2.concat(ts);
		bs3.validate();
		assertEquals(ts,bs3.subList(10, 20));
		assertEquals(ts,bs3.subList(20, 30));

		PersistentVector <Integer> bs4=bs2.concat(bs2);
		assertEquals(40,bs4.size());
		bs4.validate();
		assertEquals(ts,bs4.subList(10, 20));
		assertEquals(ts,bs4.subList(20, 30));
		assertEquals(ts,bs4.subList(30, 40));
	}
	
	@Test public void testSetTypes() {
		testPersistentSet(Sets.createFrom(new String[] {"a","b","c"}));
		testPersistentSet(NullSet.INSTANCE);
		testPersistentSet(SingletonSet.create("Bob"));
		testPersistentSet(PersistentHashMap.create().assoc(2, "Benhamma").keySet());
		testPersistentSet(PersistentHashSet.createSingleValueSet(Integer.valueOf(5)));
		testPersistentSet(PersistentHashSet.createFromSet(null));
		testPersistentSet(Sets.createFrom(new Integer[] {1}));
		testPersistentSet(Sets.createFrom(new Integer[] {1,null,3}));
	}
	
	public <T> void testPersistentSet(APersistentSet<T> a) {
		testSetInclude(a);
		testPersistentCollection(a);
	}
	
	public <T> void testPersistentCollection(APersistentCollection<T> a) {
		testDelete(a);
		testInclude(a);
		testClone(a);
		testSizing(a);
		testIterator(a);
		testPersistentObject(a);
	}
	
	public <T> void testPersistentObject(APersistentCollection<T> a) {
		a.validate();
		CommonTests.testCommonData(a);
	}
	
	public <T> void testIterator(APersistentCollection<T> a) {
		int i=0;
		for (T t : a) {
			assertTrue(a.contains(t));
			i++;
		}
		assertEquals(a.size(),i);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void testSizing(APersistentCollection<T> a) {
		assertTrue(a.size()>=0);
		T[] output=(T[]) a.toArray();
		assertEquals(a.size(),output.length);
		
		if (a.size()>0) {
			assertTrue(a.contains(Rand.pick(output)));
		}
	}
	
	public <T> void testDelete(APersistentCollection<T> a) {
		if (a==null) throw new Error("!!!");
		APersistentCollection<T> da=a.excludeAll(a);
		assertEquals(0,da.size());
		assertTrue(da.isEmpty());
		assertEquals(0,da.hashCode());
		
		int size=a.size();
		if (size>0) {
			T t=a.iterator().next();
			APersistentCollection<T> dd=a.exclude(t);
			assertTrue(dd.size()<size);
			assertTrue(!dd.contains(t));
			assertTrue(a.contains(t));
		}
	}
	
	public <T> void testClone(APersistentCollection<T> a) {
		APersistentCollection<T> ca=a.clone();
		assertTrue(ca==a);
		assertTrue(ca.equals(a));
		assertEquals(a.hashCode(),ca.hashCode());
		assertTrue(ca.getClass()==a.getClass());
		//assertTrue(ca!=a);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void testInclude(APersistentCollection<T> a) {
		Object[] ar=a.toArray();
		if (ar.length>0) {
			T v=(T)ar[Rand.r(ar.length)];
			assertTrue(a.contains(v));
			
			APersistentCollection<T> ad=a.exclude(v);
			assertFalse(ad.contains(v));
			
			APersistentCollection<T> adi=ad.include(v);		
			assertTrue("Can't find value "+v+" in: "+adi,adi.contains(v));
			assertTrue(adi.containsAll(a));
		}
	}
	
	public <T> void testSetInclude(APersistentSet<T> a) {
		if (a.size()>0) {
			APersistentSet<T> b=a.include(a.iterator().next());
			assertTrue(b.size()==a.size());
			assertTrue(b.equals(a));
		}
		
		if (a.allowsNulls()) {
			APersistentSet<T> an=a.include(null);
			assertEquals(a.size()+(a.contains(null)?0:1),an.size());
		
			APersistentSet<T> n=a.excludeAll(a);
			assertTrue(!n.contains(null));
			n=n.include(null);
			assertEquals(1,n.size());
			assertTrue(n.contains(null));
		}
	}
	
	public <T> void testPersistentList(APersistentVector<T> a) {
		a.validate();
		testSubLists(a);
		testHeadTail(a);
		testAppends(a);
		testConcats(a);
		testCuts(a);
		testDeletes(a);
		testEquals(a);
		testInserts(a);
		testExceptions(a);
		testListConversion(a);
		testPersistentCollection(a);
		testFrontBack(a);
		testHashCode(a);
		TestLists.testImmutableList(a);
	}

	public <T> void testFrontBack(APersistentVector<T> a) {
		APersistentVector<T> f=a.front();
		APersistentVector<T> b=a.back();
		
		assertEquals(a.size(),f.size()+b.size());
		assertEquals(a,f.concat(b));
	}
	
	public <T> void testHashCode(APersistentVector<T> a) {
		int ah=a.hashCode();
		
		int ih=RT.iteratorHashCode(a.iterator());
		assertEquals(ah,ih);
		
		Object[] ar=a.toArray();
		int arh=RT.arrayHashCode(ar);
		assertEquals(arh,ah);
	}
	
	public <T> void testExceptions(APersistentVector<T> a) {
		try {
			// just before start
			a.get(-1);
			fail();
		} catch (IndexOutOfBoundsException x) {/* OK */}
		
		try {
			// just after end
			a.get(a.size());
			fail();
		} catch (IndexOutOfBoundsException x) {/* OK */}
		
		try {
			// negative delete range
			a.deleteRange(2,0);
			fail();
		} catch (Exception x) {/* OK */}
		
		try {
			// negative delete position
			a.deleteRange(-3,2);
			fail();
		} catch (Exception x) {/* OK */}

		try {
			// out of range delete
			a.deleteRange(0,1000000000);
			fail();
		} catch (Exception x) {/* OK */}

		try {
			// out of range sublist - before start
			a.subList(-1,Rand.r(a.size()));
			fail();
		} catch (Exception x) {/* OK */}
		
		try {
			// out of range sublist - over limit
			a.subList(Rand.r(a.size()),a.size()+Rand.d(100));
			fail();
		} catch (Exception x) {/* OK */}
		
		try {
			// negative delete position
			a.deleteRange(-4,-4);
			fail();
		} catch (Exception x) {/* OK */}

		try {
			// copy to negative position
			a.copyFrom(-1, a, 10, 1);
			fail();
		} catch (Exception x) {/* OK */}
		
		try {
			// copy from beyond length of source
			a.copyFrom(0, a, 0, a.size()+1);
			fail();
		} catch (Exception x) {/* OK */}
		
		try {
			// clear persistent object
			a.clear();
			if (a.size()>=0) fail();
		} catch (UnsupportedOperationException x) {/* OK */}

	}

	@SuppressWarnings("unchecked")
	public <T> void testEquals(APersistentVector<T> a) {
		assertEquals(a,a.clone());
		assertEquals(a,a.concat((APersistentVector<T>)Vectors.emptyVector()));
		assertEquals(a,a.deleteRange(0,0));
	}
	
	public <T> void testDeletes(APersistentVector<T> a) {
		int start=Rand.r(a.size());
		int end=Rand.range(start, a.size());
		
		APersistentVector<T> sl=a.subList(start,end); // subList to delete
		
		APersistentVector<T> dl=a.deleteRange(start, end);
		if (start>0) {
			assertEquals(a.get(start-1),dl.get(start-1));
		}
		if (end<a.size()) {
			assertEquals(a.get(end),dl.get(start));
		}
		
		APersistentVector<T> nl=dl.insertAll(start, sl);
		
		assertEquals("Problem re-inserting into "+a.getClass()+" for initial list "+a+" and dl="+dl+" and sl="+sl,a,nl);
	}
	
	public <T> void testInserts(APersistentVector<T> a) {
		int start=Rand.r(a.size());
		APersistentVector<T> pl=a.insertAll(start, a);
		if (a.size()>0) {
			assertEquals(a.get(0),pl.get(start));
		}
	}

	public <T> void testCuts(APersistentVector<T> a) {
		APersistentVector<T> front=a.front();
		APersistentVector<T> back=a.back();
		
		assertEquals(a.size(),front.size()+back.size());
	}
	
	public <T> void testConcats(APersistentVector<T> a) {
		int n=a.size();
		APersistentVector<T> pl=a;
		
		//System.out.println(pl.getClass()+"="+pl);
		pl=Vectors.concat(pl, pl);
		//System.out.println(pl.getClass()+"="+pl);
		pl=Vectors.concat(pl, pl);
		//System.out.println(pl.getClass()+"="+pl);
		pl=Vectors.concat(pl, pl);
		//System.out.println(pl.getClass()+"="+pl);
		pl=Vectors.concat(pl, pl);
		//System.out.println(pl.getClass()+"="+pl);

		int plsize=pl.size();
		assertEquals(n*16,plsize);
		
		if (n>0) {
			for (int r=0; r<plsize; r++) {
				assertEquals("Position:"+r+" in "+pl,a.get(r%n),pl.get(r));
			}
		}
		testSubLists(pl);
	}
	
	public <T> void testAppends(APersistentVector<T> a) {
		ArrayList<T> al=new ArrayList<T>();
		
		int n=a.size();	
		for (int i=0; i<n; i++) {
			al.add(a.get(i));
		}
		
		APersistentVector<T> la=Tuple.of(null, null);
		
		APersistentVector<T> nl=la.concat(a.concat(la));
		assertEquals(n+4,nl.size());
		
		for (int i=0; i<n; i++) {
			assertTrue(RT.equals(a.get(i), nl.get(i+2)));
		}
		
		// check hash code equivalence
		APersistentVector<T> cp=Tuple.createFrom(al);		
		assertEquals(cp.hashCode(),a.hashCode());
	}

	public <T> void testHeadTail(APersistentVector<T> a) {
		if (a.size()>=1) {
			T head=a.head();
			APersistentVector<T> tail=a.tail();
			
			assertEquals(head,a.get(0));
			assertEquals(a.size()-1,tail.size());
			
			APersistentVector<T> aa=Tuple.of(head).concat(tail);
			assertEquals(a,aa);
		}
	}
	
	public <T> void testListConversion(APersistentVector<T> a) {
		APersistentList<T> list=Lists.create(a);
		assertEquals(a,Vectors.coerce(list));
	}
	
	public <T> void testSubLists(APersistentVector<T> a) {
		int n=a.size();
		for (int i=0; i<10; i++) {
			int b=Rand.r(n);
			int c=Rand.range(b, n);
			APersistentVector<T> sl=a.subList(b, c);
			int sll=c-b;
			assertEquals(sll,sl.size());
			if (sll>0) {
				int r=Rand.range(0,sll-1);
				assertEquals(sl.get(r),a.get(b+r));
			}
		}
		
		assertTrue(a==a.subList(0,n));
		int rp=Rand.r(n);
		assertEquals(EmptyVector.INSTANCE,a.subList(rp,rp));
		
		// zero length copyFrom
		assertEquals(a,a.copyFrom(Rand.r(n), a, Rand.r(n), 0));

		// safe length copyFrom
		a.copyFrom(Rand.r(n/2), a, Rand.r(n/2), Rand.r(n/2));
	
		completelyTestRandomProperSublist(a);
	}
	
	public <T> void completelyTestRandomProperSublist(APersistentVector<T> a) {
		int size=a.size();
		if (size<=1) return;
		
		int b=Rand.r(size-1);
		int c=Rand.range(b+1,(b==0)?(size-1):size);
		
		APersistentVector<T> sl=a.subList(b,c);
		int n=Rand.r(sl.size());
		assertEquals("Getting from "+sl.getClass()+sl+" at position "+n,a.get(b+n),sl.get(n));
		
		completelyTestRandomProperSublist(sl);
	}
	
	@Test public void testRepeats() {
		APersistentVector<Integer> tl=(Tuple.of(1,1,1,1,1));
		APersistentVector<Integer> rl=(RepeatVector.create(1, 5));
		assertEquals(tl,rl);
		
		APersistentVector<Integer> t2=(Tuple.of(2,2,2,2,2));
		t2=rl.copyFrom(2, t2, 2, 2);
		assertEquals(Tuple.of(1,1,2,2,1),t2);
	}
	
	@Test public void testDeleting() {
		APersistentVector<Integer> tl=(Tuple.of(1,2,3,4,5));
		APersistentVector<Integer> ol=(Tuple.of(1,3,5));
		APersistentVector<Integer> pl=tl;
		pl=pl.exclude(2);
		pl=pl.exclude(4);
		assertEquals(ol,pl);
		assertEquals(ol,tl.deleteAt(1).deleteAt(2));
	}


}