package magic.analyser;

import magic.data.Symbol;
import magic.expression.Constant;
import magic.expression.Expression;
import magic.expression.Lookup;
import magic.lang.Context;

public class Analyser {

	public static Expression<?> analyse(Object form) {
		return analyse(Context.EMPTY,form);
	}
	
	public static Expression<?> analyse(Context c, Object form) {
		if (form instanceof Symbol) return analyseSymbol(c,(Symbol)form);
		return Constant.create(form);
	}

	private static Expression<?> analyseSymbol(Context c, Symbol sym) {
		return Lookup.create(sym);
	}

}
