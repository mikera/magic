package magic.type;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import magic.RT;
import magic.Type;

@RunWith(Parameterized.class)
public class TestTypes1Param {
	@Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data=new ArrayList<>();
        for (Type t:exampleTypes) {
        	data.add(new Object[] {t});
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
		JavaType.create(Double.class),
		JavaType.create(Integer.class),
		JavaType.create(String.class),
		JavaType.create(Number.class),
		Value.create("foo"),
		Value.create(1),
		Maybe.create(JavaType.create(Integer.class)),
		Maybe.create(JavaType.create(String.class)),
		Not.create(JavaType.create(Integer.class)),
		Not.create(Value.create("foo")),
		ValueSet.create(new Object[] {1, "foo"}),
		FunctionType.create(Something.INSTANCE, Something.INSTANCE),
		FunctionType.create(Something.INSTANCE),
		FunctionType.create(Something.INSTANCE, JavaType.create(Number.class)),
		Intersection.create(Null.INSTANCE,Value.create("notNull")),
		Union.create(Value.of("foo"), ValueSet.of("baz","bar"))
	};
	
	static final Object[] testObjects={
			null,
			0,
			1,
			true,
			false,
			"Foo",
			1.0,
			new Object(),
			Anything.INSTANCE};

	
	private Type type;
	
    public TestTypes1Param(Object input) {
        type=(Type)input;
    }
	
	@Test public void testTypes() {
		assertNotNull(type);
	}
	
	@Test 
	public void testObjectProperties() {
		Type a=type;
			
		for (Object o: testObjects) {
			assertTrue(a.checkInstance(o)!=a.inverse().checkInstance(o));
			if (a.checkInstance(o)) {
				Type ot=RT.inferType(o);
				assertTrue(a.intersection(ot).checkInstance(o));
				assertTrue(ot.intersection(a).checkInstance(o));
			}
		}	
	}
	
	@Test 
	public void testProperties() {
		Type a=type;
		a.validate();
			
		if (a.canBeNull()) assertTrue(a.checkInstance(null));
		if (a.cannotBeNull()) assertFalse(a.checkInstance(null));	
		
		if (a.canBeFalsey()) assertTrue("Issue with canBeFalsey with: "+a, 
				a.checkInstance(null)||a.checkInstance(Boolean.FALSE));
	}
}
