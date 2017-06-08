package magic;

import magic.compiler.AExpander;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.type.*;

/**
 * Static constant types
 * 
 * @author Mike
 */
public class Types {
	public static final JavaType<Number> NUMBER = JavaType.NUMBER;
	public static final JavaType<String> STRING = JavaType.STRING;
	public static final JavaType<Boolean> BOOLEAN = JavaType.BOOLEAN;
	public static final JavaType<magic.data.Symbol> SYMBOL = JavaType.SYMBOL;
	public static final JavaType<magic.data.Keyword> KEYWORD = JavaType.KEYWORD;

	public static final Anything ANYTHING = Anything.INSTANCE;
	public static final Something SOMETHING = Something.INSTANCE;
	public static final Nothing NOTHING = Nothing.INSTANCE;
	public static final Null NULL = Null.INSTANCE;
	public static final Type TYPE = JavaType.MAGIC_TYPE;
	public static final Type PREDICATE = FunctionType.create(BOOLEAN, ANYTHING);
	public static final Type FORM = JavaType.OBJECT;
	public static final JavaType<APersistentVector<?>> VECTOR = JavaType.create(APersistentVector.class);
	public static final JavaType<APersistentVector<?>> LIST = JavaType.create(APersistentList.class);
	public static final JavaType<APersistentSet<?>> SET = JavaType.create(APersistentSet.class);
	public static final JavaType<APersistentMap<?,?>> MAP = JavaType.create(APersistentMap.class);
	public static final JavaType<AExpander> EXPANDER = JavaType.create(AExpander.class);;
	
	public static Type intersect(Type a, Type b) {
		if (a==null) return b;
		if (b==null) return a;
		return a.intersection(b);
	}
}
