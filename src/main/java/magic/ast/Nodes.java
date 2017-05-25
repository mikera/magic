package magic.ast;

import magic.fn.IFn1;

public class Nodes {

	public static final IFn1<Node<?>,Object> TO_FORM = n -> ((Node<?>)n).toForm();

}
