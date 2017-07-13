package magic.compiler;

import magic.RT;
import magic.Symbols;
import magic.Type;
import magic.ast.Apply;
import magic.ast.Cast;
import magic.ast.Constant;
import magic.ast.ContextAction;
import magic.ast.Define;
import magic.ast.Do;
import magic.ast.Expander;
import magic.ast.HashMap;
import magic.ast.If;
import magic.ast.InstanceOf;
import magic.ast.InvokeReflective;
import magic.ast.InvokeStaticReflective;
import magic.ast.Lambda;
import magic.ast.Let;
import magic.ast.List;
import magic.ast.ListForm;
import magic.ast.Lookup;
import magic.ast.Loop;
import magic.ast.Node;
import magic.ast.Quote;
import magic.ast.Recur;
import magic.ast.Return;
import magic.ast.Set;
import magic.ast.Vector;
import magic.data.APersistentList;
import magic.data.APersistentMap;
import magic.data.APersistentVector;
import magic.data.Keyword;
import magic.data.Lists;
import magic.data.Maps;
import magic.data.Symbol;
import magic.data.Vectors;
import magic.fn.IFn;
import magic.lang.Context;
import magic.lang.Slot;

/**
 * A standard set of expanders used to transform raw AST nodes into compilable
 * AST
 * 
 * These expanders are available in the RT.BOOTSTRAP_CONTEXT, and represent the
 * basic special forms and constructs necessary to bootstrap the Magic
 * environment.
 * 
 * @author Mike
 */
public class Expanders {

	/**
	 * The initial expander.
	 * 
	 * This expander expands sub forms using other expanders where appropriate,
	 * and continues to expand using the same initial expander
	 */
	public static final AExpander INITAL_EXPANDER = new DefaultExpander();

	private static final class DefaultExpander extends AExpander {
		@Override
		public Node<?> expand(Context c, Node<?> form, AExpander ex) {
			if (form instanceof ListForm) {
				return listExpand(c, (ListForm) form, ex);
			}
			if (form instanceof Vector) {
				return vectorExpand(c, (Vector<?>) form, ex);
			}
			
			if (form instanceof Lookup) {
				return lookupExpand(c,(Lookup<?>)form);
			}
			
			// note the things we can leave unchanged:
			// - Maps and sets are read in as HashMap and Set nodes
			// - constant literals and keywords are already Constant nodes		
			return form;
		}

		private Node<?> lookupExpand(Context c, Lookup<?> form) {
			//Symbol sym = form.getSymbol();
			//Symbol resSym=RT.resolveSym(c, sym);
			//return Lookup.create(resSym,form.meta());
			return form;
		}

		private Node<?> vectorExpand(Context c, Vector<?> form, AExpander ex) {
			int n = form.size();
			Node<?>[] forms = new Node[n];
			for (int i = 0; i < n; i++) {
				forms[i] = ex.expand(c, form.get(i), ex);
			}
			return Vector.create(Lists.wrap(forms), form.getSourceInfo());
		}

		private Node<?> listExpand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n == 0)
				return ListForm.EMPTY;
			Node<?> head = form.get(0);

			if (head.isSymbol()) {
				Symbol sym = head.getSymbol();
				Slot<Object> slot = RT.resolveSlot(c,sym);
				// handle nested expander
				if ((slot != null) && slot.isExpander()) {
					AExpander e = (AExpander) slot.getValue(); 
					return e.expand(c, form, ex).includeDependency(RT.resolveSym(c,sym));
				}

				// handle .someMethod forms
				if ((!sym.isQualified()) && sym.getName().startsWith(".")) {
					String memberName = sym.getName().substring(1);
					Symbol memberSym = Symbol.create(memberName);
					SourceInfo si = head.getSourceInfo();
					ListForm newForm = ListForm.createCons(Lookup.create(Symbols.DOT, si), form.get(1),
							Lookup.create(memberSym, si), form.subList(2, n), form.getSourceInfo());
					return ex.expand(c, newForm, ex);
				}
			}

			return applicationExpand(c, form, ex);
		}

		private Node<?> applicationExpand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			Node<?>[] forms = new Node[n];
			for (int i = 0; i < n; i++) {
				forms[i] = ex.expand(c, form.get(i), ex);
			}
			return Apply.create(Lists.wrap(forms), form.meta());
		}
	}

	/**
	 * An expander that expands def forms
	 */
	public static final AExpander DEF = new DefExpander();

	private static final class DefExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n != 3)
				throw new ExpansionException("Can't expand def, requires at least a symbolic name and expression", form);

			Node<?> symForm=form.get(1);
			Node<?> nameObj = ex.expand(c, symForm,ex);
			if (!nameObj.isSymbol()) {
				throw new AnalyserException("Can't expand def: requires a symbol as name but got: "+RT.className(nameObj), form);
			}
			Symbol name = nameObj.getSymbol();

			// note we don't expand here, we need the original form in case of
			// redefines
			Node<?> exp = form.get(2);
			// Node<?> exp=ex.expand(c, form.get(2), ex);

			APersistentMap<Keyword,Object> meta=form.meta();
			return Define.create(name, exp, meta);
		}
	}

	/**
	 * An expander that expands do forms
	 */
	public static final AExpander DO = new DoExpander();

	private static final class DoExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 1)
				throw new ExpansionException("Can't expand do, requires at least a do symbol", form);

			APersistentList<Node<?>> body = form.getNodes().subList(1, n);

			body = (APersistentList<Node<?>>) ex.expandAll(c, body, ex);

			APersistentMap<Keyword,Object> meta=form.meta();
			return Do.create(body, meta);
		}
	}
	
	/**
	 * An expander that expands cast forms
	 */
	public static final AExpander CAST = new CastExpander();

	private static final class CastExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n !=3)
				throw new ExpansionException("Can't expand cast, requires a type and expression", form);

			Node<?> typeNode = ex.expand(c,form.get(1),ex);	
			// TODO: check if this is sane?
			Type type=(Type) Compiler.eval(c, typeNode).getValue();
			Node<?> exp = ex.expand(c, form.get(2), ex);

			APersistentMap<Keyword,Object> meta=form.meta();
			return Cast.create(type, exp,meta);
		}
	}
	
	/**
	 * An expander that expands dot forms
	 */
	public static final AExpander DOT = new DotExpander();

	private static final class DotExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int fn = form.size();
			if (fn < 3)
				throw new ExpansionException(
						"Can't expand dot, requires at least an instance or classname and method call or member symbol",
						form);

			APersistentMap<Keyword, Object> meta=form.meta();

			Node<?> inst = ex.expand(c, form.get(1), ex);

			Node<?> op = form.get(2);
			// make the method call into a list if it is flattened
			if (!(op instanceof ListForm))
				op = ListForm.create(form.getNodes().subList(2, fn),Maps.empty());

			ListForm call = (ListForm) op;
			int n = call.size();
			Node<?> nameObj = call.get(0);
			if (!nameObj.isSymbol()) {
				throw new ExpansionException("Can't expand dot: requires a symbolic method name", form);
			}
			Symbol method = nameObj.getSymbol();

			APersistentList<Node<?>> args = call.getNodes().subList(1, n);
			APersistentList<Node<?>> argsExpanded = (APersistentList<Node<?>>) ex.expandAll(c, args, ex);

			// check for a static invoke
			if (inst.isSymbol()) {
				Symbol s = inst.getSymbol();
				String name = s.getName();
				if ((!s.isQualified()) && RT.maybeClassName(name)) {
					Class<?> cl = RT.classForName(name);
					if (cl != null) {
						return InvokeStaticReflective.create(cl, method, argsExpanded.toArray(new Node<?>[n - 1]), meta);
					}
				}
			}
		
			return InvokeReflective.create(inst, method, argsExpanded.toArray(new Node<?>[n - 1]), meta);
		}
	}

	/**
	 * An expander that expands defn forms
	 */
	public static final AExpander DEFN = new DefnExpander();

	private static final class DefnExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 3) {
				throw new ExpansionException("Can't expand defn, requires at least function name and arg vector", form);
			}
			
			Node<?> nameObj = form.get(1);
			if (!nameObj.isSymbol()) {
				throw new ExpansionException("Can't expand defn: requires a symbolic function name", form);
			}
			Node<?> argObj = form.get(2);

			APersistentMap<Keyword,Object> meta=form.meta();
			// get the body. Don't expand yet: fn does this
			APersistentList<Node<?>> body = form.getNodes().subList(3, n);

			// create the (fn [...] ...) form
			APersistentList<Node<?>> fnList = Lists.cons(Lookup.create(Symbols.FN), argObj, body);
			ListForm fnDef = ListForm.create(fnList, meta);

			@SuppressWarnings("unchecked")
			ListForm newForm = ListForm.create(Lists.of(Lookup.create(Symbols.DEF), nameObj, fnDef), meta);
			return ex.expand(c, newForm, ex);
		}
	}

	/**
	 * An expander that expands fn forms
	 */
	public static final AExpander FN = new FnExpander();

	private static final class FnExpander extends AListExpander {
		@SuppressWarnings("unchecked")
		@Override
		public Lambda<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 2)
				throw new ExpansionException("Can't expand fn, requires at least an arg vector", form);

			Node<?> argObj = ex.expand(c, form.get(1), ex);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand fn: requires a vector of arguments but got " + argObj + " of type "+argObj.getClass(), form);
			}

			// expand the body
			APersistentList<Node<?>> body = (APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(2, n),
					ex);

			APersistentMap<Keyword,Object> meta=form.meta();
			return Lambda.create((Vector<Symbol>) argObj, body, meta);
		}
	}
	
	/**
	 * An expander that expands return forms
	 */
	public static final AExpander RETURN = new ReturnExpander();

	private static final class ReturnExpander extends AListExpander {
		@Override
		public Return<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n != 2)
				throw new ExpansionException("Can't expand return, requires a single return value e.g.: (return :foo)", form);

			Node<?> rExp = ex.expand(c, form.get(1),ex);

			APersistentMap<Keyword,Object> meta=form.meta();
			return Return.create(rExp, meta);
		}
	}
	
	/**
	 * An expander that expands recur forms
	 */
	public static final AExpander RECUR = new RecurExpander();

	private static final class RecurExpander extends AListExpander {
		@Override
		public Recur<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n <1)
				throw new ExpansionException("Can't expand recur, invalid form: ", form);

			APersistentList<Node<?>> exps = form.getNodes().subList(1, n);
			APersistentVector<Node<?>> expsVec = Vectors.coerce(ex.expandAll(c, exps, ex));
			
			APersistentMap<Keyword,Object> meta=form.meta();
			return Recur.create(expsVec, meta);
		}
	}

	/**
	 * An expander that expands expander forms
	 */
	public static final AExpander EXPANDER = new ExpanderExpander();

	private static final class ExpanderExpander extends AListExpander {
		@SuppressWarnings("unchecked")
		@Override
		public Expander expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 2)
				throw new ExpansionException("Can't expand expander, requires at least an arg vector", form);

			Node<?> argObj = form.get(1);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand expander: requires a vector of arguments but got: " + argObj,
						form);
			}
			Vector<?> aov = (Vector<?>) argObj;
			if (aov.size() != 2) {
				throw new AnalyserException(
						"Can't expand expander: must have two parameters for binding (continuation expander and expander params) but got: "
								+ argObj,
						form);
			}

			Node<?> exObj = aov.get(0);
			if (!exObj.isSymbol()) {
				throw new AnalyserException(
						"Can't expand expander: first parameter must be a symbol to bind to continuation expander but got: "
								+ exObj,
						form);
			}
			Symbol exSym = exObj.getSymbol();

			Node<?> paramObj = aov.get(1);
			if (!(paramObj instanceof Vector)) {
				throw new AnalyserException(
						"Can't expand expander: second parameter must be a vector of expander parameters but got: "
								+ paramObj,
						form);
			}

			SourceInfo si = form.getSourceInfo();
			// expand the body
			APersistentList<Node<?>> body = (APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(2, n),
					ex);

			return Expander.create(exSym, (Vector<Symbol>) paramObj, body, si);
		}
	}

	/**
	 * An expander that expands let forms
	 */
	public static final AExpander LET = new LetExpander();

	private static final class LetExpander extends AListExpander {
		@SuppressWarnings("unchecked")
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 2)
				throw new ExpansionException("Can't expand let, requires at least a binding vector", form);

			SourceInfo si = form.getSourceInfo();

			Node<?> argObj = form.get(1);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand let: requires a vector of bindings but got " + argObj, form);
			}

			// expand the body
			APersistentList<Node<?>> body = (APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(2, n),
					ex);

			return Let.create((Vector<Object>) argObj, body, si);
		}
	}
	
	/**
	 * An expander that expands loop forms
	 */
	public static final AExpander LOOP = new LoopExpander();

	private static final class LoopExpander extends AListExpander {
		@SuppressWarnings("unchecked")
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 2)
				throw new ExpansionException("Can't expand loop, requires at least a binding vector", form);

			SourceInfo si = form.getSourceInfo();

			Node<?> argObj = form.get(1);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand loop: requires a vector of bindings but got " + argObj, form);
			}

			// expand the body
			APersistentList<Node<?>> body = (APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(2, n),
					ex);

			return Loop.create((Vector<Object>) argObj, body, si);
		}
	}

	/**
	 * An expander that expands vector forms
	 */
	public static final AExpander VECTOR = new VectorExpander();

	private static final class VectorExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 1)
				throw new ExpansionException("Can't expand vector! No form present??", form);

			SourceInfo si = form.getSourceInfo();
			APersistentList<Node<?>> body = form.getNodes().subList(1, n);

			APersistentVector<Node<?>> bodyVec = Vectors.coerce(ex.expandAll(c, body, ex));

			return Vector.create(bodyVec, si);
		}
	}
	
	/**
	 * An expander that expands list forms
	 */
	public static final AExpander LIST = new ListExpander();

	private static final class ListExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 1)
				throw new ExpansionException("Can't expand list! No form present??", form);

			SourceInfo si = form.getSourceInfo();
			APersistentList<Node<?>> body = form.getNodes().subList(1, n);

			APersistentVector<Node<?>> bodyVec = Vectors.coerce(ex.expandAll(c, body, ex));
 
			return List.create(bodyVec, si);
		}
	}
	
	/**
	 * An expander that expands 'context' forms
	 */
	public static final AExpander CONTEXT = new InContextExpander();

	private static final class InContextExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n !=2)
				throw new ExpansionException("Can't expand in-context, requires exactly one context expression", form);

			APersistentList<Node<?>> body = Lists.coerce(ex.expandAll(c, form.getNodes(),ex));
			APersistentMap<Keyword,Object> meta=form.meta();
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ContextAction<?> icNode=new ContextAction(body,
					new ContextAction.Action() {
						@Override
						public EvalResult eval(Context context, APersistentMap bindings,Object[] args) {
							return EvalResult.create((Context)args[0], null);
						}
					},meta);
			
			return icNode;
		}
	}
	
	/**
	 * An expander that expands 'ns' forms
	 */
	public static final AExpander NS = new NSExpander();

	private static final class NSExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n !=2)
				throw new ExpansionException("Can't expand ns, requires exactly one context expression", form);

			APersistentList<Node<?>> body = Lists.coerce(ex.expandAll(c, form.getNodes(),ex));
			APersistentMap<Keyword,Object> meta=form.meta();
			
			if (!body.get(1).isSymbol()) {
				throw new ExpansionException("Can't expand ns, requires a symbol as namespace argument", form);
			}
			Symbol nSym=body.get(1).getSymbol();
			
			// the sublist thing is a hack to stop the ns symbol getting evaluated
			// probably OK?
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ContextAction<?> icNode=new ContextAction(body.subList(0, 1),
					new ContextAction.Action() {
						@Override
						public EvalResult eval(Context context, APersistentMap bindings,Object[] args) {
							context=context.define(Symbols._NS_, Constant.create(nSym.getName()));
							return EvalResult.create(context, null);
						}
					},meta);
			
			return icNode;
		}
	}

	/**
	 * An expander that expands set forms
	 */
	public static final AExpander SET = new SetExpander();

	private static final class SetExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 1)
				throw new ExpansionException("Can't expand set! No form present??", form);

			SourceInfo si = form.getSourceInfo();
			APersistentList<Node<?>> body = form.getNodes().subList(1, n);

			APersistentVector<Node<?>> bodyVec = Vectors.coerce(ex.expandAll(c, body, ex));

			return Set.create(bodyVec, si);
		}
	}

	/**
	 * An expander that expands hashmap forms
	 */
	public static final AExpander HASHMAP = new HashMapExpander();

	private static final class HashMapExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 1)
				throw new ExpansionException("Can't expand map! No form present??", form);

			SourceInfo si = form.getSourceInfo();
			APersistentList<Node<?>> body = form.getNodes().subList(1, n);

			APersistentVector<Node<?>> bodyVec = Vectors.coerce(ex.expandAll(c, body, ex));

			return HashMap.create(bodyVec, si);
		}
	}

	/**
	 * An expander that expands quoted forms
	 */
	public static final AExpander QUOTE = new QuoteExpander();

	private static final class QuoteExpander extends AListExpander {
		@Override
		public Quote expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n != 2)
				throw new ExpansionException("Can't expand quote, requires a form", form);

			Node<?> qsym = form.get(0);
			if (!qsym.isSymbol()) throw new ExpansionException("Quote expansion expecting a symbol?",form);
			boolean syntaxQuote = qsym.getSymbol().equals(Symbols.SYNTAX_QUOTE);
			Node<?> quotedNode = form.get(1);

			SourceInfo si = form.getSourceInfo();

			Quote newForm = Quote.create(quotedNode, syntaxQuote, si);
			return newForm;
		}
	}

	/**
	 * An expander that expands unquote forms
	 */
	public static final AExpander UNQUOTE = new UnquoteExpander();

	private static final class UnquoteExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n != 2)
				throw new ExpansionException("Can't expand unquote, requires a form with one expression", form);

			Node<?> unquotedNode = form.get(1);

			// Execute the code to create a resulting form
			
			Object resultForm= Compiler.eval(c, unquotedNode).getValue();
			Node<?> analysed=Analyser.analyse(resultForm);
			
			// return an expanded form
			Node<?> expanded=ex.expand(c, analysed, ex);
			return expanded;
		}
	}

	public static final AExpander DEFMACRO = new DefMacroExpander();

	private static final class DefMacroExpander extends AListExpander {
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 3)
				throw new ExpansionException("Can't expand defmacro, requires at least macro name and arg vector",
						form);

			Node<?> nameObj = form.get(1);
			if (!nameObj.isSymbol()) {
				throw new AnalyserException("Can't expand defmacro: requires a symbolic function name in: ", form);
			}
			Node<?> argObj = ex.expand(c, form.get(2), ex);

			SourceInfo si = form.getSourceInfo();
			// get the body. Don't expand yet: fn does this
			APersistentList<Node<?>> body = form.getNodes().subList(3, n);

			// create the (fn [...] ...) form
			APersistentList<Node<?>> fnList = Lists.cons(Lookup.create(Symbols.MACRO), argObj, body);
			ListForm fnDef = ListForm.create(fnList, si);

			@SuppressWarnings("unchecked")
			ListForm newForm = ListForm.create(Lists.of(Lookup.create(Symbols.DEF), nameObj, fnDef), si);
			return ex.expand(c, newForm, ex);
		}
	};

	/**
	 * Expander for `if` forms.
	 */
	public static final IfExpander IF = new IfExpander();;

	private static final class IfExpander extends AListExpander {

		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			SourceInfo si = form.getSourceInfo();
			if ((n < 3) || (n > 4))
				throw new ExpansionException(
						"Can't expand if: requires a condition, a true expression and optional false expression", form);
			Node<?> test = ex.expand(c, form.get(1), ex);
			Node<?> trueExp = ex.expand(c, form.get(2), ex);
			Node<?> falseExp = (n == 4) ? ex.expand(c, form.get(3), ex) : Constant.create(null, si);
			return If.createIf(test, trueExp, falseExp, si);
		}
	}
	
	public static final Object INSTANCEOF = new InstanceOfExpander();

	private static final class InstanceOfExpander extends AListExpander {

		@SuppressWarnings("unchecked")
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			SourceInfo si = form.getSourceInfo();
			if (n != 3) {
				throw new ExpansionException("Can't expand instance?: requires a type and a value", form);
			}
			
			Node<?> typeNode=ex.expand(c, form.get(1), ex);
			Node<?> expNode=ex.expand(c, form.get(2), ex);
			
			return InstanceOf.create((Node<Type>) typeNode, expNode, si);
		}
		
	}

	/**
	 * Expander for `macro` forms. Creates a new expander with the semantics of
	 * a Clojure macro, i.e. works as a transformation of source data objects.
	 * 
	 * TODO: Conform if this means losing some of the benefits of types?
	 */
	public static final AExpander MACRO = new MacroExpander();

	private static final class MacroExpander extends AListExpander {
		@SuppressWarnings("unchecked")
		@Override
		public Node<?> expand(Context c, ListForm form, AExpander ex) {
			int n = form.size();
			if (n < 3)
				throw new ExpansionException("Can't expand macro: requires at least an arg vector and body", form);

			Node<?> argObj = ex.expand(c, form.get(1), ex);
			if (!(argObj instanceof Vector)) {
				throw new AnalyserException("Can't expand macro: requires a vector of arguments: ", form);
			}

			SourceInfo si = form.getSourceInfo();
			APersistentList<Node<?>> body = (APersistentList<Node<?>>) ex.expandAll(c, form.getNodes().subList(2, n),
					ex);
			
			APersistentMap<Keyword, Object> meta = form.meta();
			Lambda<Object> macroFn = Lambda.create((Vector<Symbol>) argObj, body, meta);
			
			IFn<Object> fn = (IFn<Object>) macroFn.compute(c);
			magic.compiler.MacroExpander me = magic.compiler.MacroExpander.create(fn);
			return Constant.create(me, si);
		}
	};
}
