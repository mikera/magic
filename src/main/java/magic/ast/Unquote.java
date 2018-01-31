package magic.ast;

import magic.RT;
import magic.Symbols;
import magic.Type;
import magic.Types;
import magic.compiler.Analyser;
import magic.compiler.AnalysisContext;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node representing an unquoted form i.e. (unquote ....)
 * 
 * Unquote evaluates to a the value of the unquoted form when evaluated in a quoted context
 * 
 * This may possibly include unquoted elements
 * @author Mike
 *
 */
public class Unquote extends BaseForm<Object> {

	private final Node<Object> form;
		
	@SuppressWarnings("unchecked")
	public Unquote(Node<Object> form, APersistentMap<Keyword, Object> meta) {
		super (Lists.of(Lookup.create(Symbols.UNQUOTE),form),meta);
		this.form=form;
	}
	
	@Override
	public Node<Object> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Unquote(form,meta);
	}
	
	/**
	 * Create a Quote AST Node from a node
	 * @param sourceInfo
	 * @param syntaxQuote2
	 * @param pop
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Unquote create(Node<? extends Object> node, APersistentMap<Keyword, Object> meta) {
		return new Unquote((Node<Object>)node,meta);
	}

	@Override
	public EvalResult<Object> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		return form.eval(context,bindings);
	}
	
	/**
	 * Unquotes need to be evaluated during analysis
	 */
	@Override
	public Node<?> analyse(AnalysisContext context) {
		Node<?> analysed=super.analyse(context);
		Object createdForm=analysed.evalQuoted(context.getContext(),Maps.empty(), false).getValue();
		// TODO: don't need any bindings, should have been eliminated?
		return Analyser.analyse(createdForm);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<Object> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<Object> newForm=(Node<Object>) fn.apply(form);
		return (form==newForm)?this:create(newForm,meta());
	}
	
	/**
	 * When evaluated in a quoted context, result the result of evaluating the quoted form
	 */
	@Override
	public EvalResult<Object> evalQuoted(Context context, APersistentMap<Symbol, Object> bindings,
			boolean syntaxQuote) {
		return form.eval(context, bindings);
	}

	@Override
	public Type getType() {
		return Types.FORM;
	}
	
	@Override
	public String toString() {
		return "(UNQUOTE "+RT.toString(form)+")";
	}

	
}
