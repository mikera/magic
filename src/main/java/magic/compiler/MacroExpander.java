package magic.compiler;

import magic.ast.List;
import magic.ast.Node;
import magic.fn.IFn;
import magic.lang.Context;

/**
 * Expander class that wraps a function used to expand forms
 * @author Mike
 *
 */
public class MacroExpander extends AListExpander {
	private IFn<Object> fn;
	
	public MacroExpander(IFn<Object> macroFn) {
		this.fn=macroFn;
	}

	public static MacroExpander create(IFn<Object> fn2) {
		return new MacroExpander(fn2);
	}

	@Override
	public Node<?> expand(Context c, List form, AExpander ex) {
		int n=form.size()-1;
		Object[] arr=new Object[n];
		for (int i=0; i<n; i++) {
			arr[i]=form.get(i+1).compute(c);
		}
		
		Object expandedForm=fn.applyToArray(arr);
		Node<?> analysedNode=Analyser.analyse(expandedForm);
		Node<?> result=ex.expand(c, analysedNode,ex);
		return result;
	}



}
