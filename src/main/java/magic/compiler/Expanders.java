package magic.compiler;

import magic.data.IPersistentList;
import magic.data.Lists;
import magic.data.PersistentList;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Slot;

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
	
	


}
