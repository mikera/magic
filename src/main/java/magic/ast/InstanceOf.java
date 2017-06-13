package magic.ast;

import magic.RT;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * A node that performs a runtime instance check against a Magic type
 * 
 * Can be optimised away if the type of the value is provably a member or not a member of the type.
 * 
 * @author Mike
 *
 */
public class InstanceOf extends BaseForm<Boolean> {
	private final Node<Type> typeExpr;
	private final Node<?> exp;

	@SuppressWarnings("unchecked")
	private InstanceOf(Node<Type> type, Node<?> exp, SourceInfo source) {
		super(Lists.of(Lookup.create(Symbols.INSTANCE_Q),type,exp), exp.getDependencies().includeAll(type.getDependencies()), source);
		this.typeExpr=type;
		this.exp=exp;
	}

	public static InstanceOf create(Node<Type> type, Node<?> exp, SourceInfo si) {
		return new InstanceOf(type, exp,si);
	}
	
	@Override
	public Node<Boolean> specialiseValues(APersistentMap<Symbol,Object> bindings) {
		return mapChildren(node -> ((Node<?>) node).specialiseValues(bindings));
	}

	@Override
	@SuppressWarnings("unchecked")
	public InstanceOf mapChildren(IFn1<Node<?>,Node<?>> fn) {
		Node<?> newExp=fn.apply(exp);
		Node<Type> newType=(Node<Type>) fn.apply(typeExpr);
		if ((newExp==exp)&&(newType==typeExpr)) return this;
		return create(newType,newExp,getSourceInfo());
	}

	@Override
	public Node<Boolean> optimise() {
		InstanceOf opt=mapChildren(node -> ((Node<?>) node).optimise());
		return opt.optimiseLocal();
	}
	
	private Node<Boolean> optimiseLocal() {
		if (typeExpr.isConstant()) {
			Type type=typeExpr.getValue();
			Type eType=exp.getType();
			if (type.contains(eType)) return Constant.create(true,getSourceInfo());
			if (!type.intersects(eType)) return Constant.create(false,getSourceInfo());
		}
		return this;
	}
	
	@Override
	public EvalResult<Boolean> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		EvalResult<Type> typeRes=this.typeExpr.eval(context, bindings);
		Type type=typeRes.getValue();
		EvalResult<?> res=exp.eval(typeRes.getContext(), bindings);
		return new EvalResult<Boolean>(res.getContext(),type.checkInstance(res.getValue()));
	}

	@Override
	public String toString() {
		return "(instance? "+RT.toString(typeExpr)+" "+RT.toString(exp)+ ")";
	}

	
	
}
