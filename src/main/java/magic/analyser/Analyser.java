package magic.analyser;

import magic.expression.Constant;
import magic.expression.Expression;
import magic.lang.Context;

public class Analyser {

	public static Expression<?> analyse(Context c, Object form) {
		return Constant.create("Analysed Expression placeholder");
	}

}
