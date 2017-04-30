package magic.lang;

import static org.junit.Assert.*;

import org.junit.Test;

import magic.expression.Apply;
import magic.expression.Constant;
import magic.fn.IFn2;

public class TestExpression {
	/**
	 * An empty Context for testing purposes
	 */
	static final Context ec=Context.EMPTY;
	
	@Test public void testConstant() {
		Constant<String> c=Constant.create("Foo");
		assertEquals("Foo",c.compute(ec));
	}
	
	@Test public void testApply() {
		IFn2<Integer> f=new IFn2<Integer>() {
			@Override
			public Integer apply(Object o1, Object o2) {
				return (Integer)o1+(Integer)o2;
			}	
		};
		
		Apply<Integer> app=Apply.create(Constant.create(f),Constant.create(1),Constant.create(2));
		assertEquals((Integer)3,app.compute(ec));
	}
}
