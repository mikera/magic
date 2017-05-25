package magic.compiler;

import magic.ast.Apply;
import magic.ast.Constant;
import magic.ast.Define;
import magic.ast.Lambda;
import magic.ast.List;
import magic.ast.Node;
import magic.ast.Quote;
import magic.ast.Vector;
import magic.data.APersistentList;
import magic.data.Lists;
import magic.data.Symbol;
import magic.fn.IFn;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.Symbols;

public class Expanders {

	/**
	 * An expander that simply expands the form and continues to analyse using the same initial expander
	 */
	public static final Expander INITAL_EXPANDER = new DefaultExpander();
	
	private static final class DefaultExpander extends Expander {
		@Override
		public Node<?> expand(Context c, Node<?> form,Expander ex) {
			if (form instanceof List) {
				return listExpand(c,(List)form,ex);
			}
			if (form instanceof Vector) {
				return vectorExpand(c,(Vector<?>)form,ex);
			}

			return form;
		}

		private Node<?> vectorExpand(Context c, Vector<?> form, Expander ex) {
			int n=form.size();
			Node<?>[] forms=new Node[n];
			for (int i=0; i<n; i++) {
				forms[i]=ex.expand(c,form.get(i),ex);
			}		
			return Vector.create(Lists.wrap(forms),form.getSourceInfo());
		}

		private Node<?> listExpand(Context c, List form, Expander ex) {
			int n=form.size();
			if (n==0) return List.EMPTY;
			Node<?> head=form.get(0);
			if (head.isConstant()) {
				Object h=head.getValue();
				if (h instanceof Symbol) {
					Slot<Object> slot=c.getSlot((Symbol)h);
					if (slot.isExpander(c)) {
						Expander e=(Expander) slot.getValue(c);
						return e.expand(c, form, ex);
					}
				}
			}
			
			return applicationExpand(c,form,ex);
		}
		
		private Node<?> applicationExpand(Context c, List form, Expander ex) {
			int n=form.size();
			Node<?>[] forms=new Node[n];
			for (int i=0; i<n; i++) {
				forms[i]=ex.expand(c,form.get(i),ex);
			}		
			return Apply.create(Lists.wrap(forms),form.getSourceInfo());
		}
	}
	
	/**
	 * An expander that expands def forms
	 */
	public static final Expander DEF = new DefExpander();

	private static final class DefExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, List form,Expander ex) {
			int n=form.size();
			if (n!=3) throw new ExpansionException("Can't expand def, requires a symbolic name and expression",form);
			
			Node<?> nameObj=form.get(1);
			if (!(nameObj.isConstant()&&nameObj.getValue() instanceof Symbol)) {
				throw new AnalyserException("Can't expand def: requires a symbolic name in: ",form);
			}
			Symbol name=(Symbol)nameObj.getValue();
			
			Node<?> exp=ex.expand(c, Analyser.analyse(c,form.get(2)), ex);
			
			SourceInfo si=form.getSourceInfo();
			return Define.create(name, exp, si);
		}
	}
	
	/**
	 * An expander that expands defn forms
	 */
	public static final Expander DEFN = new DefnExpander();

	private static final class DefnExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, List form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionException("Can't expand defn, requires at least function name and arg vector",form);
			
			Node<?> nameObj=form.get(1);
			if (!(nameObj.isConstant()&&nameObj.getValue() instanceof Symbol)) {
				throw new ExpansionException("Can't expand defn: requires a symbolic function name in: ",form);
			}
			Node<?> argObj=ex.expand(c, Analyser.analyse(c,form.get(2)), ex);
			
			SourceInfo si=form.getSourceInfo();
			// get the body. Don't expand yet: fn does this
			APersistentList<Node<?>> body=form.getNodes().subList(2,n);
			
			// create the (fn [...] ...) form
			APersistentList<Node<?>> fnList=Lists.cons(Constant.create(Symbols.FN),argObj,body);
			List fnDef=List.create(fnList,si);
			
			@SuppressWarnings("unchecked")
			List newForm=List.create(Lists.of(Constant.create(Symbols.DEF), nameObj,fnDef),si);
			return ex.expand(c, newForm, ex);
		}
	}
	
	/**
	 * An expander that expands fn forms
	 */
	public static final Expander FN = new FnExpander();

	private static final class FnExpander extends AListExpander {
		@SuppressWarnings("unchecked")
		@Override
		public Lambda<?> expand(Context c, List form,Expander ex) {
			int n=form.size();
			if (n<2) throw new ExpansionException("Can't expand fn, requires at least an arg vector",form);
			
			Node<?> argObj=ex.expand(c, Analyser.analyse(c,form.get(1)), ex);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand fn: requires a vector of arguments: ",form);
			}
			
			SourceInfo si=form.getSourceInfo();
			// expand the body
			APersistentList<Node<?>> body=(APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(1,n),ex);
			
			return Lambda.create((Vector<Symbol>)argObj, body,si);
		}
	}

	/**
	 * An expander that expands quoted forms
	 */
	public static final Expander QUOTE = new QuoteExpander();

	private static final class QuoteExpander extends AListExpander {
		@Override
		public Quote expand(Context c, List form,Expander ex) {
			int n=form.size();
			if (n!=2) throw new ExpansionException("Can't expand quote, requires a form",form);
			
			Node<?> qsym=form.get(0);
			boolean syntaxQuote=qsym.getValue().equals(Symbols.SYNTAX_QUOTE);
			Node<?> quotedNode=form.get(1);
			
			SourceInfo si=form.getSourceInfo();
			
			Quote newForm=Quote.create(quotedNode,syntaxQuote,si);
			return newForm;
		}
	}

	public static final Expander DEFMACRO = new AListExpander() {
		@Override
		public Node<?> expand(Context c,List form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionException("Can't expand defmacro, requires at least macro name and arg vector",form);
			
			Node<?> nameObj=form.get(1);
			if (!(nameObj.isConstant()&&nameObj.getValue() instanceof Symbol)) {
				throw new AnalyserException("Can't expand defn: requires a symbolic function name in: ",form);
			}
			Node<?> argObj=ex.expand(c, Analyser.analyse(c,form.get(2)), ex);
			
			SourceInfo si=form.getSourceInfo();
			// get the body. Don't expand yet: fn does this
			APersistentList<Node<?>> body=form.getNodes().subList(2,n);
			
			// create the (fn [...] ...) form
			APersistentList<Node<?>> fnList=Lists.cons(Constant.create(Symbols.MACRO),argObj,body);
			List fnDef=List.create(fnList,si);
			
			@SuppressWarnings("unchecked")
			List newForm=List.create(Lists.of(Constant.create(Symbols.DEF), nameObj,fnDef),si);
			return ex.expand(c, newForm, ex);
		}
	};

	public static final Expander MACRO = new AListExpander() {
		@SuppressWarnings("unchecked")
		@Override
		public Node<?> expand(Context c, List form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionException("Can't expand macro, requires at least an arg vector and body",form);
			
			Node<?> argObj=ex.expand(c, Analyser.analyse(c,form.get(1)), ex);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand macro: requires a vector of arguments: ",form);
			}
			
			SourceInfo si=form.getSourceInfo();
			APersistentList<Node<?>> body=(APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(1,n),ex);
			
			Lambda<Object> macroFn= Lambda.create((Vector<Symbol>)argObj, body,si);
			IFn<Object> fn=(IFn<Object>) macroFn.compute(c);
			MacroExpander me= MacroExpander.create(fn);
			return Constant.create(me);
		}
	};
}
