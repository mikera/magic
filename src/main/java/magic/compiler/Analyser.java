package magic.compiler;

import magic.RT;
import magic.ast.Apply;
import magic.ast.Constant;
import magic.ast.Define;
import magic.ast.Do;
import magic.ast.Dot;
import magic.ast.If;
import magic.ast.Lambda;
import magic.ast.Let;
import magic.ast.Lookup;
import magic.ast.Node;
import magic.ast.Quote;
import magic.ast.Vector;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.IPersistentVector;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.fn.AFn;
import magic.fn.ArityException;
import magic.lang.Context;
import magic.lang.Symbols;

/**
 * Magic code analyser
 * Responsible for converting forms into AST representations
 * 
 * @author Mike
 *
 */
public class Analyser {

	/**
	 * Analyses a form in an empty context. Useful mainly for debug / test purposes
	 * @param form
	 * @return
	 */
	public static <T> Node<T> analyse(Object form) {
		return analyse(RT.INITIAL_CONTEXT,form);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Node<T>[] analyseAll(Context c, APersistentList<Object> forms) {
		int n=forms.size();
		Node<?>[] exps=new Node<?>[n];
		for (int i=0; i<n; i++) {
			exps[i]=analyse(c,forms.get(i));
		}
		return (Node<T>[]) exps;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Node<T> analyse(Context c, Object form) {
		if (form==null) return (Node<T>) Constant.NULL;
		if (form instanceof APersistentList) return analyseList(c,(APersistentList<Object>)form);
		if (form instanceof IPersistentVector) return (Node<T>) analyseVector(c,(IPersistentVector<Object>)form);
		if (form instanceof Symbol) return analyseSymbol(c,(Symbol)form);
		
		// fall through handles constant literals, keywords etc

		return (Node<T>) Constant.create(form);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseList(Context c, APersistentList<Object> form) {
		int n=form.size();
		if (n==0) return (Node<T>) Constant.create(Lists.EMPTY);
		
		Object first=form.head();
		if (first instanceof Symbol) return analyseSymbolApplication(c,form);
		
		return Apply.create(analyse(c,first),analyseAll(c,form.tail()));
	}

	private static <T> Node<T> analyseSymbolApplication(Context c, APersistentList<Object> form) {
		Symbol first=(Symbol) form.head();
		APersistentList<Object> tail=form.tail();
		
		if (first==Symbols.DEF) return analyseDefine(c,(Symbol)tail.head(),tail.tail());
		if (first==Symbols.FN) return analyseFn(c,tail.head(),tail.tail());
		if (first==Symbols.QUOTE) return analyseQuote(c,form,false);
		if (first==Symbols.SYNTAX_QUOTE) return analyseQuote(c,form,true);
		if (first==Symbols.DO) return analyseDo(c,tail);
		if (first==Symbols.IF) return analyseIf(c,tail);
		if (first==Symbols.LET) return analyseLet(c,tail);
		if (first==Symbols.DOT) return analyseDot(c,form);
		if (first==Symbols.EXPANDER) return analyseExpander(c,form);
		
		
		return Apply.create(Lookup.create(first),analyseAll(c,tail));
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseDot(Context c, APersistentList<Object> form) {
		int n=form.size();
		if (n<3) throw new AnalyserException("dot special form requires at least an instance and a method name symbol",form);
		Object symObj=form.get(2);
		if (!(symObj instanceof Symbol)) new AnalyserException("dot special form requires a method name symbol",form);
		Symbol method=(Symbol) symObj;
		Node<?> inst=analyse(c,form.get(1));
		int nArgs=n-3;
		Node<Object>[] args=new Node[nArgs];
		for (int i=0; i<nArgs; i++) {
			args[i]=analyse(c,form.get(i+3));
		}
		return (Node<T>)Dot.create(inst, method, args);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseExpander(Context c, APersistentList<Object> form) {
		int n=form.size();
		if (n<3) throw new AnalyserException("expander requires at least an argument vector and an expansion body",form);
		Object argObj=form.get(1);
		if (!(argObj instanceof IPersistentVector<?>)) {
			throw new AnalyserException("expander expects a vector [ex [args ...]]",argObj);
		}
		IPersistentVector<Object> args=(IPersistentVector<Object>) argObj;
		if (args.size()!=2) {
			throw new AnalyserException("expander expects arguments of the form [ex [args ...]]",argObj);
		}
		Symbol exSym=(Symbol) args.get(0);
		IPersistentVector<Object> binds=(IPersistentVector<Object>) args.get(1);
		int nBinds=binds.size();
		
		Node<T> fNode=analyseFn(c,args.get(1),form.subList(2, n));
		
		return (Node<T>) Constant.create(new AListExpander() {
			@Override
			public Object expand(Context c, APersistentList<Object> form, Expander ex) {
				if (form.size()!=nBinds+1) {
					throw new AnalyserException("Wrong number of args passed to exander, expected "+nBinds,form);
				}
				Object[] vals=new Object[nBinds];
				for (int i=0; i<nBinds; i++) vals[i]=form.get(i+1);
				
				APersistentMap<Symbol, Object> bnds=Maps.create(exSym, ex);
				AFn<Object> fn= (AFn<Object>) fNode.eval(c, bnds).getValue();
				return fn.applyTo(vals);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseLet(Context c, APersistentList<Object> forms) {
		Object head=forms.head();
		if (!(head instanceof IPersistentVector)) throw new AnalyserException("First argument to let must be a binding vector",head);
		IPersistentVector<Object> bindingVector=(IPersistentVector<Object>) head;
		int vSize=bindingVector.size();
		int n=vSize/2;
		if (n*2!=vSize) throw new Error("let binging vector must contain an even number of forms");
		Symbol[] syms=new Symbol[n];
		Node<?>[] exps=new Node<?>[n];
		for (int i=0; i<n; i++) {
			syms[i]=(Symbol)bindingVector.get(2*i);
			exps[i]=analyse(c,bindingVector.get(2*i+1));
		}
		
		APersistentList<Object> bodyForms=forms.tail();
		int nBody=bodyForms.size();
		Node<?>[] body=new Node<?>[nBody];
		for (int i=0; i<nBody; i++) {
			body[i]=analyse(c,bodyForms.get(i));
		}
		
		return Let.create(syms, exps, body);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseDo(Context c,APersistentList<Object> forms) {
		int n=forms.size();
		Node<T>[] exs=new Node[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,forms.get(i));
		}		
		return Do.create(exs);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseIf(Context c,APersistentList<Object> forms) {
		int n=forms.size();
		if (n<2) throw new ArityException("If requires at least one test expreesion and a true result, got "+n);
		if (n>3) throw new ArityException("Too many subexpressions for if, got "+n);
		Node<Object> test=analyse(c,forms.get(0));
		Node<T> trueExp=analyse(c,forms.get(1));
		Node<T> falseExp=(n>2)?analyse(c,forms.get(2)):(Node<T>) Constant.NULL;
		return If.createIf(test,trueExp,falseExp);
	}

	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseQuote(Context c, APersistentList<Object> form, boolean syntaxQuote) {
		if (form.size()!=2) throw new Error("Quote expects a single form");
		return (Node<T>) Quote.create(form.get(1),syntaxQuote,((Symbol)form.head()).symbolSet());
	}
 
	@SuppressWarnings("unchecked")
	private static <T> Node<T> analyseFn(Context c, Object arglist, APersistentList<Object> tail) {
		int n=tail.size();
		if (n==0) throw new Error("No function body definition");
		if (!(arglist instanceof APersistentVector)) throw new Error("fn requires a vector argument list, got: "+arglist);
		APersistentVector<?> args=(APersistentVector<?>)arglist;
		
		Node<T>[] exs=new Node[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,tail.get(i));
		}
		
		Node<?> doexp=Let.create(exs);
		
		return (Node<T>) Lambda.create((APersistentVector<Symbol>)args,doexp);
	}

	private static <T>  Node<T> analyseDefine(Context c, Symbol sym, APersistentList<Object> args) {
		if (args.size()!=1) {
			throw new Error("Define requires exactly one body, got: "+args);
		}
		return Define.create(sym,analyse(c,args.get(0)));
	}

	@SuppressWarnings("unchecked")
	private static <T> Vector<T> analyseVector(Context c, IPersistentVector<T> form) {
		int n=form.size();
		Node<T>[] exs=new Node[n];
		for (int i=0; i<n; i++) {
			exs[i]=analyse(c,form.get(i));
		}
		
		IPersistentVector<Node<T>> exps=Vectors.wrap(exs);
		return Vector.create(exps);
	}

	private static <T> Node<T> analyseSymbol(Context c, Symbol sym) {
		return Lookup.create(sym);
	}

	/**
	 * Expands a form in a given context
	 * @param context
	 * @param form
	 * @return
	 */
	public static Object expand(Context context, Object form) {
		Expander ex=Expanders.INITAL_EXPANDER;
		return ex.expand(context, form, ex);
	}


}
