package magic.compiler;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import static org.junit.Assert.*;
import static org.objectweb.asm.ClassWriter.*;

public class TestASM {

	MyClassLoader cl=new MyClassLoader() ;
	
	private static final class MyClassLoader extends ClassLoader {
		
		public Class<?> define(byte[] bcode) {
			return defineClass(null,bcode, 0, bcode.length);
		}
	};
	
	@Test public void testClassCreation() {
		ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
		cw.visit(52,
				Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,  // access
				"magic/Test",                            // classname
				null,                                    // signature, not needed unless generic?
			    "java/lang/Object",                      // superclass
			    new String[] {}                          // interfaces
		);
		
		cw.visitEnd();
		
		byte[] bcode=cw.toByteArray();
		
		Class<?> klass=cl.define(bcode);
		
		assertNotNull(klass);
	}
}
