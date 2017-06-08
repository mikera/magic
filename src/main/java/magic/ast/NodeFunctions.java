package magic.ast;

import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
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

	/**
	 * Maps a function across all nodes in a list
	 * Guarantees to return the same list if no nodes are changed
	 * @param nodes
	 * @param fn
	 * @return
	 */
	public static APersistentList<Node<? extends Object>> mapAll(APersistentList<Node<? extends Object>> nodes,
			IFn1<Node<?>, Node<?>> fn) {
		int n=nodes.size();
		APersistentList<Node<? extends Object>> newNodes=nodes;
		for (int i=0; i<n; i++) {
			Node<?> node=nodes.get(i);
			Node<?> newNode=fn.apply(node);
			if (node!=newNode) newNodes=newNodes.assocAt(i, newNode);
		}
		return newNodes;
	}

	/**
	 * Maps a function across all nodes in a vector
	 * Guarantees to return the same vector if no nodes are changed
	 * @param nodes
	 * @param fn
	 * @return
	 */
	public static APersistentVector<Node<?>> mapAll(APersistentVector<Node<?>> exps, IFn1<Node<?>, Node<?>> fn) {
		int nExps=exps.size();
		APersistentVector<Node<?>> newExps=exps;
		for (int i=0; i<nExps; i++) {
			Node<?> node=exps.get(i);
			Node<?> newNode=fn.apply(node);
			if (node!=newNode) {
				newExps=newExps.assocAt(i,newNode);
			} 
		}
		return newExps;
	}
}
