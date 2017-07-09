package magic;

import magic.data.Symbol;

/**
 * Utility class defining built-in Magic symbols
 * 
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
	public static final Symbol RETURN = Symbol.create("return");
	public static final Symbol LOOP = Symbol.create("loop");
	public static final Symbol RECUR = Symbol.create("recur");

	// special symbols
	public static final Symbol UNDERSCORE = Symbol.create("_");
	public static final Symbol DOT = Symbol.create(".");
	public static final Symbol AMPERSAND = Symbol.create("&");
	public static final Symbol NIL = Symbol.create("nil");

	// macros and expanders
	public static final Symbol DEFN = Symbol.create("defn");
	public static final Symbol MACRO = Symbol.create("macro");
	public static final Symbol DEFMACRO = Symbol.create("defmacro");
	public static final Symbol EXPANDER = Symbol.create("expander");
	public static final Symbol DEFEXPANDER = Symbol.create("defexpander");
	
	// core functions
	public static final Symbol PRINTLN = Symbol.create("println");
	public static final Symbol SET = Symbol.create("set");
	public static final Symbol HASHMAP = Symbol.create("hashmap");
	public static final Symbol VECTOR = Symbol.create("vector");
	public static final Symbol LIST = Symbol.create("list");
	public static final Symbol PLUS = Symbol.create("+");
	public static final Symbol EQUALS =  Symbol.create("=");;
	
	// types and casting
	public static final Symbol INSTANCE_Q = Symbol.create("instance?");
	public static final Symbol CAST = Symbol.create("cast");
	public static final Symbol ANY = Symbol.create("Any");
	public static final Symbol NONE = Symbol.create("None");
	public static final Symbol NULL = Symbol.create("Null");
	public static final Symbol U = Symbol.create("U");
	public static final Symbol N = Symbol.create("N");

	
	// environment variables and bindings
	public static final Symbol _NS_ = Symbol.create("magic.core","*ns*");
	public static final Symbol _CONTEXT_ = Symbol.create("*context*");
	public static final Symbol CONTEXT = Symbol.create("context");
	public static final Symbol NS = Symbol.create("ns");


	
	
}
