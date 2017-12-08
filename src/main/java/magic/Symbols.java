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
	public static final Symbol DEF=Symbol.createCore("def");
	public static final Symbol FN = Symbol.createCore("fn");
	public static final Symbol FNS = Symbol.createCore("fns");
	public static final Symbol DO = Symbol.createCore("do");
	public static final Symbol IF =  Symbol.createCore("if");
	public static final Symbol LET = Symbol.createCore("let");
	public static final Symbol QUOTE = Symbol.createCore("quote");
	public static final Symbol UNQUOTE = Symbol.createCore("unquote");
	public static final Symbol UNQUOTE_SPLICING = Symbol.createCore("unquote-splicing");
	public static final Symbol SYNTAX_QUOTE = Symbol.createCore("syntax-quote");;
	public static final Symbol RETURN = Symbol.createCore("return");
	public static final Symbol LOOP = Symbol.createCore("loop");
	public static final Symbol RECUR = Symbol.createCore("recur");
	public static final Symbol DOT = Symbol.createCore(".");

	// special symbols - not namespaced
	public static final Symbol UNDERSCORE = Symbol.create("_");
	public static final Symbol AMPERSAND = Symbol.create("&");
	public static final Symbol NIL = Symbol.create("nil");

	// macros and expanders
	public static final Symbol DEFN = Symbol.createCore("defn");
	public static final Symbol MACRO = Symbol.createCore("macro");
	public static final Symbol DEFMACRO = Symbol.createCore("defmacro");
	public static final Symbol EXPANDER = Symbol.createCore("expander");
	public static final Symbol DEFEXPANDER = Symbol.createCore("defexpander");
	
	// core functions
	public static final Symbol PRINTLN = Symbol.createCore("println");
	public static final Symbol SET = Symbol.createCore("set");
	public static final Symbol HASHMAP = Symbol.createCore("hashmap");
	public static final Symbol VECTOR = Symbol.createCore("vector");
	public static final Symbol LIST = Symbol.createCore("list");
	public static final Symbol PLUS = Symbol.createCore("+");
	public static final Symbol EQUALS =  Symbol.createCore("=");;
	
	// types and casting
	public static final Symbol INSTANCE_Q = Symbol.createCore("instance?");
	public static final Symbol CAST = Symbol.createCore("cast");
	public static final Symbol ANY = Symbol.createCore("Any");
	public static final Symbol NONE = Symbol.createCore("None");
	public static final Symbol NULL = Symbol.createCore("Null");
	public static final Symbol U = Symbol.createCore("U");
	public static final Symbol N = Symbol.createCore("N");

	
	// environment variables and bindings
	public static final Symbol _NS_ = Symbol.createCore("*ns*");
	public static final Symbol _CONTEXT_ = Symbol.createCore("*context*");
	public static final Symbol IN_CONTEXT = Symbol.create("in-context");
	public static final Symbol CONTEXT = Symbol.createCore("context");
	public static final Symbol NS = Symbol.createCore("ns");
	public static final Symbol IN_NS = Symbol.createCore("in-ns");
	public static final Symbol SYMBOL = Symbol.createCore("symbol");



	
	
}
