package magic.type;

import magic.Type;

public abstract class AFunctionType extends Type {
	
	
	public abstract boolean isVariadic();
	
	public Type[] getParamTypes() {
		int n=getMinArity();
		if (isVariadic()) n++;
		Type[] ts=new Type[n];
		for (int i=0; i<n; i++) {
			ts[i]=getParamType(i);
		}
		return ts;
	}

	protected abstract int getMinArity();

}
