package magic.ast;

import magic.Keywords;
import magic.RT;
import magic.compiler.EvalResult;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.Keyword;
import magic.data.Symbol;
import magic.fn.AFn;
import magic.fn.IFn1;
import magic.fn.MultiFn;
import magic.lang.Context;

/**
 * AST node representing a multiple-function lambda expression a.k.a. "(fn ([...] ...) ([...] ...) ....)"
 * 
 * Supports variadic args in the form (fn [a b & more] ...)
 * 
 * @author Mike
 *
 * @param <T> The return type of the lambda function
 */
public class Lambdas<T> extends BaseForm<AFn<T>> {

	private Lambdas(APersistentList<Node<?>> nodes, APersistentMap<Keyword,Object> meta) {
		super(nodes,meta);
	}

	@Override
	public Lambdas<T> withMeta(APersistentMap<Keyword, Object> meta) {
		return new Lambdas<T>(nodes,meta);
	}
	
	public static <T> Lambdas<T> create(APersistentList<Node<?>> fns, APersistentMap<Keyword,Object> meta) {
		meta=meta.assoc(Keywords.DEPS, calcDependencies(fns));
		return new Lambdas<T>(fns,meta);
	}
	
	@Override
	public Lambdas<T> mapChildren(IFn1<Node<?>, Node<?>> fn) {
		APersistentList<Node<?>> newNodes=nodes.map(fn);
		return (newNodes==nodes)?this:create(nodes,meta());
	}

	/**
	 * Computes the function object represented by this lambda. 
	 * 
	 * Specialises the body expression according to the provided context and bindings
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EvalResult<AFn<T>> eval(Context context,APersistentMap<Symbol, Object> bindings) {
		APersistentList<Node<?>> lambdas=nodes;
		int n=lambdas.size();
		AFn<T>[] fns=(AFn<T>[]) new AFn<?>[n];
		for (int i=0; i<n; i++) {
			fns[i]=(AFn<T>) lambdas.get(i).eval(context, bindings).getValue();
		}
		// System.out.println(body);
		AFn<T> fn=new MultiFn<T>(fns);
		return new EvalResult<AFn<T>>(context,fn);
	}
	
	@Override
	public String toString() {
		return "(FNS "+RT.toString(nodes.subList(1)," ")+")";
	}
	
}
