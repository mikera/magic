package magic;

import magic.type.*;

/**
 * Static constant types
 * 
 * @author Mike
 */
public class Types {
	public static final JavaType<?> NUMBER = JavaType.NUMBER;
	public static final JavaType<?> STRING = JavaType.STRING;
	public static final JavaType<?> BOOLEAN = JavaType.BOOLEAN;
	public static final JavaType<?> SYMBOL = JavaType.SYMBOL;
	public static final JavaType<?> KEYWORD = JavaType.KEYWORD;

	public static final Anything ANYTHING = Anything.INSTANCE;
	public static final Something SOMETHING = Something.INSTANCE;
	public static final Nothing NOTHING = Nothing.INSTANCE;
	public static final Null NULL = Null.INSTANCE;
	public static final Type TYPE = JavaType.MAGIC_TYPE;
	
	public static Type intersect(Type a, Type b) {
		if (a==null) return b;
		if (b==null) return a;
		return a.intersection(b);
	}
}
