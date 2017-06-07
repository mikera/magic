package magic.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import magic.RT;
import magic.Type;
import magic.Types;
import magic.compiler.AExpander;
import magic.compiler.Expanders;

public class TypeTests {

	@Test public void testFnType() {
		FunctionType t=FunctionType.create(Null.INSTANCE,JavaType.create(Integer.class));
		assertEquals(1,t.getMinArity());
		assertFalse(t.isVariadic());
		assertEquals(1,t.getParamTypes().length);
		
		assertTrue(t.contains(FunctionType.create(Null.INSTANCE,JavaType.create(Number.class))));
		assertFalse(t.contains(FunctionType.create(Null.INSTANCE,JavaType.create(String.class))));
	}
	
	@Test public void testExactValue() {
		assertTrue(Null.INSTANCE==RT.inferType(null));
	}

	
	@Test public void testExpanderType() {
		assertTrue(Types.EXPANDER.checkInstance(Expanders.DO));
		assertTrue(Types.EXPANDER.contains(Value.create(Expanders.DEFN)));
		assertTrue(Types.EXPANDER.contains(JavaType.create(AExpander.class)));
	}
	
	@Test public void testIntersectionOverlap() {
		Type i1=Intersection.create(Not.create(JavaType.create(Integer.class)),Something.INSTANCE);
		Type i2=Intersection.create(Not.create(JavaType.create(Integer.class)),Something.INSTANCE);
		
		assertTrue(i1.contains(i2));
	}
	
	@Test public void testJavaClass() {
		assertEquals(Integer.class,Intersection.create(JavaType.create(Integer.class),JavaType.create(Number.class)).getJavaClass());
		assertEquals(Integer.class,Intersection.create(JavaType.create(Number.class),JavaType.create(Integer.class)).getJavaClass());

		assertEquals(Number.class,Union.create(JavaType.create(Long.class),JavaType.create(Float.class)).getJavaClass());
		assertEquals(Object.class,Union.create(JavaType.create(BigDecimal.class),JavaType.create(String.class)).getJavaClass());
		assertEquals(Number.class,Union.create(JavaType.create(BigDecimal.class),JavaType.create(Number.class)).getJavaClass());
		
		assertTrue("Interface type should contain concrete classes", 
				JavaType.create(Collection.class).contains(JavaType.create(ArrayList.class)));
	}
	
	@Test public void testJavaInterface() {
		assertTrue(JavaType.create(List.class).checkInstance(new ArrayList<Object>()));
		
		
		assertTrue("Superinterface type should contain descendant interface type",
				JavaType.create(Collection.class).contains(JavaType.create(List.class)));
	}
	
	@Test public void testParse() {
//		assertEquals(Integer.class,Type.parse("java.lang.Integer").getJavaClass());
//		assertEquals(Number.class,Type.parse("(U Integer java.lang.Long)").getJavaClass());
	}

	@Test public void testBooleans() {
//		assertTrue(Constant.TRUE.getType().canBeTruthy());
//		assertTrue(Constant.FALSE.getType().canBeFalsey());
//		assertFalse(Constant.TRUE.getType().cannotBeTruthy());
//		assertFalse(Constant.FALSE.getType().cannotBeFalsey());
//		assertTrue(Constant.TRUE.getType().cannotBeFalsey());
//		assertTrue(Constant.FALSE.getType().cannotBeTruthy());
//		assertFalse(Constant.TRUE.getType().canBeFalsey());
//		assertFalse(Constant.FALSE.getType().canBeTruthy());
	}
	
	@Test public void testOddIntersections() {
		assertTrue(Intersection.create(Null.INSTANCE).checkInstance(null));
		assertFalse(Intersection.create(Null.INSTANCE,JavaType.create(Integer.class)).checkInstance(null));
	}
	
	@Test public void testMiscUnions() {
		assertEquals(Null.INSTANCE,Union.create(Null.INSTANCE));
		assertEquals(JavaType.create(Number.class),Union.create(JavaType.create(Number.class),JavaType.create(Integer.class)));
		assertEquals(JavaType.create(String.class),Union.create(Nothing.INSTANCE,JavaType.create(String.class)));
	}
	
	@Test public void testCast() {
//		try {
//			Cast cast=Cast.create(String.class, Constant.create(10));
//			fail("Cast should throw exception if cast is not possible");
//		} catch (KissException ke) {
//			// OK!
//		}
		
	}
}
