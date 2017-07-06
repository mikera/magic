package magic.ast;

import magic.Keywords;
import magic.RT;
import magic.Symbols;
import magic.Type;
import magic.Types;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentSet;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node representing a quoted form i.e. (quote ....) or (syntax-quote ....)
 * 
 * Quote evaluates to a form data structure.
 * 
 * This may possibly include unquoted elements
 * @author Mike
 *
 */
public class Quote extends BaseForm<Object> {

	private final Node<Object> form;
	private final boolean syntaxQuote;
	
	private static final APersistentSet<Symbol> QUOTE_SET=Sets.of(Symbols.QUOTE);
	private static final APersistentSet<Symbol> SYNTAX_QUOTE_SET=Sets.of(Symbols.SYNTAX_QUOTE);

	protected static Symbol quoteSymbol(boolean syntaxQuote) {
		return syntaxQuote?Symbols.SYNTAX_QUOTE:Symbols.QUOTE;
	}
	
	@SuppressWarnings("unchecked")
	public Quote(Node<Object> form, boolean syntaxQuote, APersistentMap<Keyword, Object> meta) {
		super (Lists.of(Lookup.create(quoteSymbol(syntaxQuote)),form),meta);
		this.syntaxQuote=syntaxQuote;
		this.form=form;
	}
	
	@Override
	public Node<Object> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Quote(form,syntaxQuote,meta);
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
		APersistentMap<Keyword, Object> meta=Maps.create(Keywords.DEPS, syms);
		meta=meta.assoc(Keywords.SOURCE, sourceInfo);
		return new Quote((Node<Object>)node,syntaxQuote,meta);
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
	public Node<?> mapChildren(IFn1<Node<?>,Node<?>> fn) {
		// TODO: should map over unquoted nodes?
		return this;
	}
	
	@Override
	public String toString() {
		return (syntaxQuote?"(syntax-quote ":"(quote ")+RT.toString(form)+")";
	}

	
}
