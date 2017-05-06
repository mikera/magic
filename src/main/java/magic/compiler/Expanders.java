package magic.compiler;

import magic.ast.Constant;
import magic.ast.Node;
import magic.fn.Expander;
import magic.lang.Context;
import magic.lang.Symbols;

public class Expanders {

	/**
	 * An expander that simply expands the form and continues to analyse using the same initial expander
	 */
	public static final Expander<?> INITAL_EXPANDER = new Expander<Object>() {
		
		@Override
		public Node<Object> expand(Context c, Object form, Object ex) {
			return Analyser.analyse(c,form);
		}
	
	};
	
	public static final Expander<?> QUOTE = new Expander<Object>(){

		@Override
		public Node<Object> expand(Context c, Object form, Object ex) {
			// TODO deal with unquote etc.
			return Constant.create(form,Symbols.QUOTE.symbolSet());
		}
	};
	


}
