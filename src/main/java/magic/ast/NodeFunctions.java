package magic.ast;

import magic.compiler.AnalysisContext;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Symbol;
import magic.fn.IFn1;

/**
 * Functions used for AST node transformations
 * 
 * @author Mike
 */
public class NodeFunctions {
	
	public static abstract class NodeFunction implements IFn1<Node<?>, Node<?>> {

		@Override
		public final Node<?> apply(Object o) {
			return apply((Node<?>)o);
		}
		
		public abstract Node<?> apply(Node<?> node);
		
	}

	/**
	 * A node function that specialises bindings
	 * @param bindings
	 * @return
	 */
	public static IFn1<Node<?>, Node<?>> specialiseValues(APersistentMap<Symbol, Object> bindings) {
		return new NodeFunction() {
			@Override
			public Node<?> apply(Node<?> node) {
				return node.specialiseValues(bindings);
			}
		};
	}
	
	/**
	 * A node function that analyses nodes
	 * @param bindings
	 * @return
	 */
	public static IFn1<Node<?>, Node<?>> analyse(AnalysisContext context) {
		return new NodeFunction() {
			@Override
			public Node<?> apply(Node<?> node) {
				return node.analyse(context);
			}
		};
	}

	public static IFn1<Node<?>, Node<?>> OPTIMISE=new NodeFunction() {
		@Override
		public Node<?> apply(Node<?> node) {
			return node.optimise();
		}
	};

	
	/**
	 * A node function that performs local node optimisation
	 * @param bindings
	 * @return
	 */
	public static IFn1<Node<?>, Node<?>> optimise() {
		return OPTIMISE;
	}

	/**
	 * Maps a function across all nodes in a list
	 * Guarantees to return the same list if no nodes are changed
	 * @param nodes
	 * @param fn
	 * @return
	 */
	public static APersistentList<Node<?>> mapAll(APersistentList<Node<?>> nodes,
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

	/**
	 * A function that maps nodes to form data objects
	 */
	public static final IFn1<Node<?>,Object> TO_FORM = n -> ((Node<?>)n).toForm();

	/**
	 * Maps a function across an array of nodes.
	 * Returns the same array if no nodes are changed, a new array otherwise
	 * @param args
	 * @param fn
	 * @return
	 */
	public static Node<?>[] mapAll(Node<?>[] nodes, IFn1<Node<?>, Node<?>> fn) {
		boolean changed=false;
		int n=nodes.length;
		for (int i=0; i<n; i++) {
			Node<?> node=nodes[i];
			Node<?> newNode=fn.apply(node);
			if (node!=newNode) {
				if (!changed) {
					nodes=nodes.clone();
					changed=true;
				}
				nodes[i]=newNode;
			}
		}
		return nodes;
	}
}
