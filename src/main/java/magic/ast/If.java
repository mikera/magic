package magic.ast;

import magic.RT;
import magic.compiler.Result;
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
	public Result<T> eval(Context context, APersistentMap<Symbol, Object> bindings) {
		Result<?> r = test.eval(context,bindings);
		Object testVal=r.getValue();
		context=r.getContext();
		if (RT.bool(testVal)) {
			return trueExp.eval(context, bindings);
		} else {
			return falseExp.eval(context, bindings);
			
		}
	}

	@Override
	public String toString() {
		return "(If "+test+" "+trueExp+" " +falseExp+")";
	}
}
