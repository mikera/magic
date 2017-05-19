package magic.lang;

import magic.data.Symbol;

/**
 * Utility class defining builtin magic symbols
 * @author Mike
 *
 */
public class Symbols {

	// special form symbols
	public static final Symbol DEF=Symbol.create("def");
	public static final Symbol FN = Symbol.create("fn");
	public static final Symbol DO = Symbol.create("do");
	public static final Symbol IF =  Symbol.create("if");
	public static final Symbol LET = Symbol.create("let");
	public static final Symbol QUOTE = Symbol.create("quote");
	public static final Symbol UNQUOTE = Symbol.create("unquote");
	public static final Symbol UNQUOTE_SPLICING = Symbol.create("unquote-splicing");
	public static final Symbol SYNTAX_QUOTE = Symbol.create("syntax-quote");;

	// special symbols
	public static final Symbol UNDERSCORE = Symbol.create("_");
	public static final Symbol DOT = Symbol.create(".");

	// macros and expanders
	public static final Symbol DEFN = Symbol.create("defn");
	public static final Symbol MACRO = Symbol.create("macro");
	public static final Symbol DEFMACRO = Symbol.create("defmacro");
	public static final Symbol EXPANDER = Symbol.create("expander");
	public static final Symbol DEFEXPANDER = Symbol.create("defexpander");
	
	// core functions
	public static final Symbol PRINTLN = Symbol.create("println");
	
	// environment variables and bindings
	public static final Symbol _NS_ = Symbol.create("*ns*");
	
	
}
