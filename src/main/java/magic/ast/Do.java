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
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;

/**
 * AST node representing a `do` block of sequential expressions
 * 
 * @author Mike
 *
 * @param <T>
 */
public class Do<T> extends BaseForm<T> {
	private final APersistentList<Node<?>> body;
	private final int nBody;
	
	public Do(APersistentList<Node<?>> bodyExprs,APersistentMap<Keyword,Object> meta) {
		super(Lists.cons(Constant.create(Symbols.DO),bodyExprs),meta);
		body=bodyExprs;
		nBody=bodyExprs.size();
	}
	
	@SuppressWarnings("unchecked")
	public Do(APersistentList<Node<?>> bodyExprs,SourceInfo source) {
		this(bodyExprs, 
				((APersistentMap<Keyword,Object>)Maps.EMPTY)
				.assoc(Keywords.DEPS,calcDependencies(bodyExprs))
				.assoc(Keywords.SOURCE,source));
	}
	
	@Override
	public Node<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Do<T>(body,meta);
	}

	public static <T> Do<T> create(APersistentList<Node<?>> body,SourceInfo source) {
		return new Do<T>(body,source);
	}
	
	public static <T> Node<T> create(Node<?>... body) {
		return create(PersistentList.wrap(body),null);
	}
	
	public static <T> Node<T> create(Node<?>[] body,SourceInfo source) {
		return create(PersistentList.wrap(body),source);
	}

	
	@SuppressWarnings({"unchecked"})
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		int nBody=this.nBody;
		
		EvalResult<T> r=new EvalResult<>(context,null);
		for (int i=0; i<nBody; i++) {
			r=(EvalResult<T>) body.get(i).eval(r.getContext(),bindings);
		}
		return r;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public Node<T> optimise() {
		if (nBody==0) return (Node<T>) Constant.NULL;
		if (nBody==1) return (Node<T>) body.get(0).optimise();
		return (Node<T>) mapChildren(NodeFunctions.optimise());
	}
	
	@Override
	public Node<? extends T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentList<Node<? extends Object>> newBody=body;
		for (int i=0; i<nBody; i++) {
			Node<?> node=body.get(i);
			Node<?> newNode=fn.apply(node);
			if (node!=newNode) {
				newBody=newBody.assocAt(i, newNode);
			} 
		}
		return (nodes==newBody)?this:create(newBody,getSourceInfo());
	}
	
	/**
	 * Returns the type of this `do` expression, i.e. the type of the last subexpression
	 */
	@Override
	public Type getType() {
		if (nBody==0) return Types.NULL;
		return body.get(nBody-1).getType();
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("(do ");
		for (int i=0; i<nBody; i++) {
			if (i>0) sb.append(' ');
			sb.append(RT.toString(body.get(i)));
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public APersistentList<Object> toForm() {
		return Lists.cons(Symbols.DO, body.map(NodeFunctions.TO_FORM));
	}

	




}
