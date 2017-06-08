package magic.ast;

import magic.RT;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.APersistentVector;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * AST node representing a quoted form
 * 
 * This may possibly include unquoted elements, which are analysed to produce AST replacement nodes
 * @author Mike
 *
 */
public class Quote extends Node<Object> {

	private final boolean syntaxQuote;
	private final Node<Object> form;
	
	private static final APersistentSet<Symbol> QUOTE_SET=Sets.of(Symbols.QUOTE);
	private static final APersistentSet<Symbol> SYNTAX_QUOTE_SET=Sets.of(Symbols.SYNTAX_QUOTE);

	public Quote(Node<Object> form, boolean syntaxQuote, APersistentSet<Symbol> symbolSet, APersistentMap<APersistentVector<Object>,Node<Object>> unquotes, SourceInfo source) {
		super (symbolSet,source);
		this.syntaxQuote=syntaxQuote;
		this.form=form;
	}
	
	/**
	 * Create a Quote AST Node from a node
	 * @param sourceInfo
	 * @param syntaxQuote2
	 * @param pop
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Quote create(Node<? extends Object> node, boolean syntaxQuote, SourceInfo sourceInfo) {
		// TODO fix deps?
		APersistentSet<Symbol> syms=(syntaxQuote)?SYNTAX_QUOTE_SET:QUOTE_SET;
		return new Quote((Node<Object>)node,syntaxQuote,syms,null, sourceInfo);
	}

	@Override
	public EvalResult<Object> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		// call evalQuoted on form
		// expects a Node back, with unquotes expanded, so we need to translate this into a form Object 
		return new EvalResult<Object>(context,form.evalQuoted(context,bindings,syntaxQuote).toForm());
	}
	
	@Override
	public Node<?> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		Symbol sym=(syntaxQuote)?Symbols.SYNTAX_QUOTE:Symbols.QUOTE;
		APersistentList<Node<?>> r=PersistentList.of(Lookup.create(sym),form.evalQuoted(context, bindings, syntaxQuote));
		return ListForm.create(r, getSourceInfo());
	}

	public boolean isSyntaxQuote() {
		return syntaxQuote;
	}
	
	@Override
	public Type getType() {
		return Types.FORM;
	}

	@Override
	public Node<Object> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		// TODO: specialise unquotes 
		return this;
	}

	@Override
	public Node<Object> optimise() {
		// TODO: optimise unquotes into constants?
		return this;
	}
	
	@Override
	public String toString() {
		return (syntaxQuote?"(syntax-quote ":"(quote ")+RT.toString(form)+")";
	}

	@Override
	public Object toForm() {
		return Lists.of(syntaxQuote?Symbols.SYNTAX_QUOTE:Symbols.QUOTE, form.toForm());
	}



}
