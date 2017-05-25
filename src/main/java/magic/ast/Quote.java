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
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Sets;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.lang.Context;
import magic.lang.Symbols;
import magic.type.JavaType;

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
		return new Quote((Node<Object>)node,syntaxQuote,Sets.emptySet(),null, sourceInfo);
	}

	@Override
	public EvalResult<Object> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		return form.evalQuoted(context,bindings,syntaxQuote);
	}
	
	@Override
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		Symbol sym=(syntaxQuote)?Symbols.SYNTAX_QUOTE:Symbols.QUOTE;
		APersistentList<Object> r=PersistentList.of(sym,form.evalQuoted(context, bindings, syntaxQuote));
		return new EvalResult<Object>(context,r);
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
		return (syntaxQuote?"(Syntax-Quote ":"(Quote ")+RT.toString(form)+")";
	}



}
