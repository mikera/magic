package magic.analyser;

import magic.expression.Constant;
import magic.expression.Expression;

public class Analyser {

	public static Expression<?> analyse(Object form) {
		return Constant.create("Analysed Expression placeholder");
	}

}
