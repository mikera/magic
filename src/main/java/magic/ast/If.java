package magic.ast;

import magic.RT;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.IFn1;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * AST node representing a conditional `if` expression.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class If<T> extends BaseForm<T> {
	private final Node<?> test; 
	private final Node<? extends T> trueExp; 
	private final Node<? extends T> falseExp; 
	
	@SuppressWarnings("unchecked")
	private If(APersistentList<Node<? extends Object>> nodes, SourceInfo source) {
		super(nodes, calcDependencies(nodes) ,source);
		this.test=nodes.get(1);
		this.trueExp=(Node<? extends T>) nodes.get(2);
		this.falseExp=(Node<? extends T>) nodes.get(3);
	}
	
	@SuppressWarnings("unchecked")
	private If(Node<?> test, Node<? extends T> trueExp, Node<? extends T> falseExp, SourceInfo source) {
		this(Lists.of(Lookup.create(Symbols.IF),test,trueExp,falseExp),source);
	}

	@SuppressWarnings("unchecked")
	public static <T> If<T> createIf(Node<?> test, Node<? extends T> trueExp) {
		return createIf(test,trueExp, (Node<T>)Constant.NULL,null);
	}
	
	public static <T> If<T> createIf(Node<?> test, Node<? extends T> trueExp, Node<? extends T> falseExp) {
		return createIf(test,trueExp,falseExp,null);
	}
	
	public static <T> If<T> createIf(Node<?> test, Node<? extends T> trueExp, Node<? extends T> falseExp, SourceInfo source) {
		return new If<T>(test,trueExp,falseExp,source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		EvalResult<?> r = test.eval(context,bindings);
		Object testVal=r.getValue();
		context=r.getContext();
		if (RT.bool(testVal)) {
			return (EvalResult<T>) trueExp.eval(context, bindings);
		} else {
			return (EvalResult<T>) falseExp.eval(context, bindings);
		}
	}
	
	@Override
	public Type getType() {
		return trueExp.getType().union(falseExp.getType());
	}
	
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return mapChildren(NodeFunctions.specialiseValues(bindings));
	}
	
	@Override
	public Node<? extends T> optimise() {
		If<T> newIf=mapChildren(NodeFunctions.optimise());
		return newIf.optimiseLocal();
	}
	
	public Node<? extends T> optimiseLocal() {
		Type testType=(test.isConstant())?RT.inferType(test.getValue()):test.getType();
		if (testType.cannotBeFalsey()) return trueExp;
		if (testType.cannotBeTruthy()) return falseExp;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public If<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		Node<Object> newTest=(Node<Object>) fn.apply(test);
		Node<? extends T> newTrue=(Node<? extends T>) fn.apply(trueExp);
		Node<? extends T> newFalse=(Node<? extends T>) fn.apply(falseExp);
		return ((newTest==test)&&(newTrue==trueExp)&&(newFalse==falseExp))?this:createIf(newTest,newTrue,newFalse,getSourceInfo());
	}
 
	@Override
	public String toString() {
		return "(if "+test+" "+trueExp+" " +falseExp+")";
	}
	
	@Override
	public APersistentList<Object> toForm() {
		return Lists.of(Symbols.IF, test.toForm(), trueExp.toForm(), falseExp.toForm());
	}
}
