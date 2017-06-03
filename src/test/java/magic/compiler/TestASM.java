package magic.compiler;

import static org.junit.Assert.*;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
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

		{	// static field
			FieldVisitor fv=cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "FOO", "Ljava/lang/String;", null, new String("foo"));
			fv.visitEnd();
		}

		{	// public final field
			// note that initial value is ignored for non-static fields
			FieldVisitor fv=cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, "bar", "Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		
		{	// no-arg constructor
				Method m = Method.getMethod("void <init> ()");
				GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
				mg.loadThis();
				mg.invokeConstructor(Type.getType(Object.class), m);
	
				// set a field
				mg.loadThis();
				mg.push("bar");
				mg.putField(Type.getType("magic/Test"), "bar", Type.getType(String.class));
				
				mg.returnValue();
				mg.endMethod();
		}
		cw.visitEnd();

		// Create the class
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
		
		String s;
		try {
			s=(String) klass.getField("FOO").get(null);
		} catch (Throwable t) {
			throw new Error(t);
		}
		assertEquals("foo",s);
		
		try {
			s=(String) klass.getField("bar").get(o);
		} catch (Throwable t) {
			throw new Error(t);
		}
		assertEquals("bar",s);
	}
}
