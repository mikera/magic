package magic.compiler;

import magic.data.APersistentList;
import magic.data.IPersistentList;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.data.Tuple;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.Symbols;

public class Expanders {

	/**
	 * An expander that simply expands the form and continues to analyse using the same initial expander
	 */
	public static final Expander INITAL_EXPANDER = new InitialExpander();
	
	private static final class DefnExpander extends AListExpander {
		@Override
		public Object expand(Context c, APersistentList<Object> form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionException("Can't expand defn, requires at least function name and arg vector",form);
			
			Object nameObj=ex.expand(c, form.get(1), ex);
			Object argObj=ex.expand(c, form.get(2), ex);
			
			APersistentList<Object> fnDef=PersistentList.of(Symbols.FN,argObj).concat(form.subList(3,n));
			APersistentList<Object> newForm=PersistentList.of(Symbols.DEF, nameObj,fnDef);
			return ex.expandAll(c, newForm, ex);
		}
	}

	/**
	 * An expander that expands defn forms
	 */
	public static final Expander DEFN = new DefnExpander();
	
	private static final class InitialExpander extends Expander {
		@Override
		@SuppressWarnings("unchecked")
		public Object expand(Context c, Object form,Expander ex) {
			if (form instanceof IPersistentList) return expand(c,(IPersistentList<Object>)form,ex);
			return form; 
		}

		public Object expand(Context c, IPersistentList<Object> form,Expander ex) {
			int n=form.size();
			if (n==0) return Lists.EMPTY;
			
			Object first=form.head();
			
			if (first instanceof Symbol) {
				Slot<?> slot=c.getSlot((Symbol)first);
				if ((slot!=null)&&slot.isExpander(c)) {
					Expander exp=(Expander) slot.getValue(c);
					return exp.expand(c,form,ex);
				}
			}
			
			return applicationExpand(c,form,ex);
		}

		private Object applicationExpand(Context c, IPersistentList<Object> form, Expander ex) {
			int n=form.size();
			Object[] forms=new Object[n];
			for (int i=0; i<n; i++) {
				forms[i]=expand(c,form.get(i),ex);
			}
			
			return PersistentList.wrap(forms);
		}
	}

	public static final Expander DEFMACRO = new AListExpander() {
		@Override
		public Object expand(Context c, APersistentList<Object> form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionException("Can't expand defmacro, requires at least macro name and arg vector",form);
			
			Object nameObj=ex.expand(c, form.get(1), ex);
			Object argObj=ex.expand(c, form.get(2), ex);
			
			Object fnDef=PersistentList.of(Symbols.MACRO,argObj).concat(form.subList(3,n));
			return ex.expandAll(c, PersistentList.of(Symbols.DEF, nameObj,fnDef),ex);
		}
	};

	public static final Expander MACRO = new AListExpander() {
		@Override
		public Object expand(Context c, APersistentList<Object> form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionException("Can't expand macro, requires at least an arg vector and body",form);
			
			Object argObj=form.get(1);
			
			APersistentList<Object> body=form.subList(2,n);
			APersistentList<Object> newForm=PersistentList.of((Object)Symbols.EXPANDER, Tuple.of(Symbols.UNDERSCORE,argObj));
			newForm=newForm.concat(body);
			return ex.expandAll(c, newForm,ex);
		}
	};
}
