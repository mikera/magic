package magic.lang;

import magic.data.Symbol;

/**
 * Utility class defining builtin magic symbols
 * @author Mike
 *
 */
public class Symbols {

	public static final Symbol DEF=Symbol.create("def");
	public static final Symbol QUOTE = Symbol.create("quote");
	public static final Symbol FN = Symbol.create("fn");
	public static final Symbol DO = Symbol.create("do");
	public static final Symbol IF =  Symbol.create("if");
	public static final Symbol LET = Symbol.create("let");
	public static final Symbol DEFN = Symbol.create("defn");
}
