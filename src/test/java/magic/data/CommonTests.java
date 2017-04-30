package magic.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import mikera.randomz.Randomz;

import org.junit.Test;

import magic.data.impl.RepeatVector;

public class CommonTests {
	
	@Test public void testCommonData() {
		testCommonData(Integer.valueOf(3));
		testCommonData(Short.valueOf((short)3));
		testCommonData(0.7543245);
		testCommonData(RepeatVector.create("Spam ",100));
		testCommonData(Randomz.getGenerator());
	}
	
	public static void testCommonData(Object o) {
		if (o instanceof Serializable) {
			testSerialization((Serializable)o);
		}
	}

	public static void testSerialization(Serializable s) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(bos);
			
			oos.writeObject(s);
			ByteArrayInputStream bis = new   ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois=new ObjectInputStream(bis);
			
			oos.writeObject(s);
			
			Serializable s2=(Serializable)ois.readObject();

			assertEquals(s,s2);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
	}
}
