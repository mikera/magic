package magic.data.impl;

import magic.data.ISeq;
import magic.data.PersistentHashSet;
import magic.data.PersistentSet;
import magic.data.Tools;

public abstract class BasePersistentSet<T> extends PersistentSet<T> {
	private static final long serialVersionUID = 5499036601499834158L;

	@Override
	public PersistentSet<T> conj(T value) {
		return PersistentHashSet.coerce(this).conj(value);
	}
	
	@Override
	public ISeq<T> seq() {
		return Tools.seq(this.iterator());
	}
}
