package magic.type;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import magic.Type;
import magic.data.Sets;
import magic.data.Tuple;

/**
 * Tests for various combinations of two types
 * @author Mike
 */
@RunWith(Parameterized.class)
public class TestTypes2Param {
	@Parameters(name = "{0} {1}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data=new ArrayList<>();
        for (Type t1:exampleTypes) {
        	for (Type t2: exampleTypes) {
        		data.add(new Object[] {t1,t2});
            }
        }
        return data;
    }
    
	static Type[] exampleTypes=new Type[] {
		Anything.INSTANCE,
		Something.INSTANCE,
		Nothing.INSTANCE,
		Null.INSTANCE,
		Reference.INSTANCE,
		Value.create("foo"),
		ValueSet.of("foo","bar"),
		Not.create(Value.create(1)),
		JavaType.create(String.class),
		JavaType.create(Number.class),
		Vector.create(2),
		Value.create("foo"),
		Value.create(1),
		Maybe.create(JavaType.create(String.class)),
		Not.create(JavaType.create(Integer.class)),
		ValueSet.create(new Object[] {1, "foo"}),
		FunctionType.create(Something.INSTANCE),
		FunctionType.create(Something.INSTANCE, JavaType.create(Number.class)),
		FunctionType.create(Something.INSTANCE, Something.INSTANCE, Anything.INSTANCE),
		Intersection.create(Null.INSTANCE,Value.create("notNull")),
		Union.create(Value.of("foo"), ValueSet.of("baz","bar"))
	};
	
	@SuppressWarnings("unchecked")
	static final Object[] testObjects={
			null,
			0,
			1L,
			true,
			false,
			"Foo",
			1.0,
			Sets.of(1,"Foo"),
			Tuple.of(1,"Foo"),
			new Object(),
			Anything.INSTANCE};

	
	private Type type1;
	private Type type2;
	
    public TestTypes2Param(Object t1, Object t2) {
        type1=(Type) t1;
        type2=(Type) t2;
    }
    
	@Test public void testComparisons() {
		Type a=type1;
		Type b=type2;
		if (a.equals(b)) {
			assertEquals(a.hashCode(),b.hashCode());
			assertTrue(a.equiv(b));
		}
	}
	
	@Test public void testIntersections() {
		Type a=type1;
		Type b=type2;
		Type c=a.intersection(b);
		Type d=b.intersection(a);
		for (Object o:testObjects) {
			if (a.checkInstance(o) && b.checkInstance(o)) {
				assertTrue("Intersection of "+a+" and "+b+" = "+c+ " should include "+o,c.checkInstance(o));
				assertTrue("Intersection of "+b+" and "+a+" = "+c+ " should include "+o,d.checkInstance(o));
			}
		}
	}
	
	@Test public void testUnions() {
		Type a=type1;
		Type b=type2;
		Type c=a.union(b);
			
		for (Object o:testObjects) {
			if (a.checkInstance(o) || b.checkInstance(o)) {
				assertTrue("Union of "+a+" and "+b+" = "+c+ " should include "+o,c.checkInstance(o));
			}
		}

	}
}
