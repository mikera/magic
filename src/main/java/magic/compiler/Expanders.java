package magic.compiler;

import magic.data.IPersistentList;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Slot;
import magic.lang.Symbols;

public class Expanders {

	/**
	 * An expander that simply expands the form and continues to analyse using the same initial expander
	 */
	public static final Expander INITAL_EXPANDER = new Expander() {
		
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
	
	};
	
	public static final Expander DEFN = new ListExpander() {
		@Override
		public Object expand(Context c, IPersistentList<Object> form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionFailedException("Can't expand defn, requires at least function name and arg vector",form);
			
			Object nameObj=ex.expand(c, form.get(1), ex);
			Object argObj=ex.expand(c, form.get(2), ex);
			
			Object fnDef=PersistentList.of(Symbols.FN,argObj).concat(ex.expandAll(c,form.subList(3,n),ex));
			return PersistentList.of(Symbols.DEF, nameObj,fnDef);
			
		}
	};
	
	public static final Expander DEFMACRO = new ListExpander() {
		@Override
		public Object expand(Context c, IPersistentList<Object> form,Expander ex) {
			int n=form.size();
			if (n<3) throw new ExpansionFailedException("Can't expand demacro, requires at least macro name and arg vector",form);
			
			Object nameObj=ex.expand(c, form.get(1), ex);
			Object argObj=ex.expand(c, form.get(2), ex);
			
			Object fnDef=PersistentList.of(Symbols.MACRO,argObj).concat(ex.expandAll(c,form.subList(3,n),ex));
			return PersistentList.of(Symbols.DEF, nameObj,fnDef);
		}
	};
}
