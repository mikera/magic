package magic.ast;

import magic.RT;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentMap;
import magic.data.Lists;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Symbols;

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
	public InstanceOf specialiseValues(APersistentMap<Symbol,Object> bindings) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Node<Boolean> optimise() {
		//Type eType=exp.getType();
		//if (type.contains(eType)) return Constant.create(true,getSourceInfo());
		//if (type.isDisjoint(eType)) return Constant.create(false,getSourceInfo());
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
