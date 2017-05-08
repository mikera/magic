package magic.ast;

import magic.RT;
import magic.compiler.EvalResult;
import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.lang.Context;

public class If<T> extends Node<T> {
	private final Node<Object> test; 
	private final Node<T> trueExp; 
	private final Node<T> falseExp; 
	
	private If(Node<Object> test, Node<T> trueExp, Node<T> falseExp) {
		super(test.getDependencies().includeAll(trueExp.getDependencies()).includeAll(falseExp.getDependencies()));
		this.test=test;
		this.trueExp=trueExp;
		this.falseExp=falseExp;
	}

	@SuppressWarnings("unchecked")
	public static <T> Node<T> createIf(Node<Object> test, Node<T> trueExp) {
		return createIf(test,trueExp, (Node<T>)Constant.NULL);
	}
	
	public static <T> Node<T> createIf(Node<Object> test, Node<T> trueExp, Node<T> falseExp) {
		return new If<T>(test,trueExp,falseExp);
	}

	@Override
	public EvalResult<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		EvalResult<?> r = test.eval(context,bindings);
		Object testVal=r.getValue();
		context=r.getContext();
		if (RT.bool(testVal)) {
			return trueExp.eval(context, bindings);
		} else {
			return falseExp.eval(context, bindings);
			
		}
	}
	
	@Override
	public Node<T> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		Node<Object> newTest=test.specialiseValues(bindings);
		Node<T> newTrue=trueExp.specialiseValues(bindings);
		Node<T> newFalse=falseExp.specialiseValues(bindings);
		return ((newTest==test)&&(newTrue==trueExp)&&(newFalse==falseExp))?this:createIf(newTest,newTrue,newFalse);
	}
	
	@Override
	public Node<T> optimise() {
		Node<Object> newTest=test.optimise();
		Node<T> newTrue=trueExp.optimise();
		Node<T> newFalse=falseExp.optimise();
		if (newTest.isConstant()) {
			// TODO: type check for possible truthiness / falsiness
			return RT.bool(newTest.getValue())?newTrue:newFalse;
		}
		return ((newTest==test)&&(newTrue==trueExp)&&(newFalse==falseExp))?this:createIf(newTest,newTrue,newFalse);
	}

	@Override
	public String toString() {
		return "(If "+test+" "+trueExp+" " +falseExp+")";
	}
}
