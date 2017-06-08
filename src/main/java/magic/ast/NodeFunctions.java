package magic.ast;

import magic.data.APersistentMap;
import magic.data.Symbol;
import magic.fn.IFn1;

public class NodeFunctions {
	
	public static abstract class NodeFunction implements IFn1<Node<?>, Node<?>> {

		@Override
		public final Node<?> apply(Object o) {
			return apply((Node<?>)o);
		}
		
		public abstract Node<?> apply(Node<?> node);
		
	}

	public static IFn1<Node<?>, Node<?>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return new NodeFunction() {
			@Override
			public Node<?> apply(Node<?> node) {
				return node.specialiseValues(bindings);
			}
		};
	}

	public static IFn1<Node<?>, Node<?>> optimise() {
		return new NodeFunction() {
			@Override
			public Node<?> apply(Node<?> node) {
				return node.optimise();
			}
		};
	}
}
