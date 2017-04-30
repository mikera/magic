package magic.data.impl;

import magic.data.ISeq;
import magic.data.PersistentHashSet;
import magic.data.APersistentSet;
import magic.data.Tools;

public abstract class BasePersistentSet<T> extends APersistentSet<T> {
	private static final long serialVersionUID = 5499036601499834158L;

	@Override
	public APersistentSet<T> conj(T value) {
		return PersistentHashSet.coerce(this).conj(value);
	}
	
	@Override
	public ISeq<T> seq() {
		return Tools.seq(this.iterator());
	}
}
