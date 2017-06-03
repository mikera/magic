package magic.compiler;

import static org.junit.Assert.assertNotNull;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class TestASM {

	MyClassLoader cl = new MyClassLoader();

	private static final class MyClassLoader extends ClassLoader {

		public Class<?> define(byte[] bcode) {
			return defineClass(null, bcode, 0, bcode.length);
		}
	};

	@Test
	public void testClassCreation() {
		ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
		cw.visit(52, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, // access
				"magic/Test", // classname
				null, // signature, not needed unless generic?
				"java/lang/Object", // superclass
				new String[] {} // interfaces
		);

		
		{	// no-arg constructor
				Method m = Method.getMethod("void <init> ()");
				GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
				mg.loadThis();
				mg.invokeConstructor(Type.getType(Object.class), m);
				mg.returnValue();
				mg.endMethod();
		}

		cw.visitEnd();

		byte[] bcode = cw.toByteArray();

		Class<?> klass = cl.define(bcode);

		assertNotNull(klass);

		Object o;
		try {
			o = klass.newInstance();
		} catch (InstantiationException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}
		assertNotNull(o);
	}
}
