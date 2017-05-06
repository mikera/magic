package magic.compiler;

import magic.ast.Constant;
import magic.ast.Node;
import magic.fn.Expander;
import magic.lang.Context;
import magic.lang.Symbols;

public class Expanders {

	public static final Expander<?> QUOTE = new Expander<Object>(){

		@Override
		public Node<Object> expand(Context c, Object form, Object ex) {
			// TODO deal with unquote etc.
			return Constant.create(form,Symbols.QUOTE.symbolSet());
		}
	};

}
