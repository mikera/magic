package magic.type;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import magic.Type;

@RunWith(Parameterized.class)
public class TestTypes {
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
		Nothing.INSTANCE,
		Value.create("foo"),
		ValueSet.of("foo","bar"),
		Not.create(Value.create(1)),
		JavaType.create(Double.class),
		Null.INSTANCE,
		Reference.INSTANCE,
		Intersection.create(Null.INSTANCE,Value.create("notNull")),
		Union.create(Value.of("foo"), ValueSet.of("baz","bar"))
	};
	
	private Type type;
	
    public TestTypes(Object input) {
        type=(Type)input;
    }
	
	@Test public void testTypes() {
		assertNotNull(type);
	}
}
