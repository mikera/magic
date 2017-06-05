package magic.ast;

import magic.RT;
import magic.Type;
import magic.compiler.EvalResult;
import magic.compiler.SourceInfo;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * AST node representing a condition `if` expression.
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
	
	private If(Node<?> test, Node<? extends T> trueExp, Node<? extends T> falseExp, SourceInfo source) {
		this(Lists.of(Lookup.create(Symbols.IF),test,trueExp,falseExp),source);
	}

	@SuppressWarnings("unchecked")
	public static <T> Node<T> createIf(Node<?> test, Node<? extends T> trueExp) {
		return createIf(test,trueExp, (Node<T>)Constant.NULL,null);
	}
	
	public static <T> Node<T> createIf(Node<?> test, Node<? extends T> trueExp, Node<? extends T> falseExp) {
		return createIf(test,trueExp,falseExp,null);
	}
	
	public static <T> Node<T> createIf(Node<?> test, Node<? extends T> trueExp, Node<? extends T> falseExp, SourceInfo source) {
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<Object> newTest=(Node<Object>) test.specialiseValues(bindings);
		Node<? extends T> newTrue=trueExp.specialiseValues(bindings);
		Node<? extends T> newFalse=falseExp.specialiseValues(bindings);
		return ((newTest==test)&&(newTrue==trueExp)&&(newFalse==falseExp))?this:createIf(newTest,newTrue,newFalse);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Node<? extends T> optimise() {
		Node<Object> newTest=(Node<Object>) test.optimise();
		Node<? extends T> newTrue=trueExp.optimise();
		Node<? extends T> newFalse=falseExp.optimise();
		if (newTest.isConstant()) {
			// TODO: type check for possible truthiness / falsiness
			return RT.bool(newTest.getValue())?newTrue:newFalse;
		}
		return ((newTest==test)&&(newTrue==trueExp)&&(newFalse==falseExp))?this:createIf(newTest,newTrue,newFalse);
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
