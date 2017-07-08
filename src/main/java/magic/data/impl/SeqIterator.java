package magic.data.impl;

import java.util.Iterator;

import magic.data.ISeq;

public class SeqIterator<T> implements Iterator<T> {

	private ISeq<T> source;
	
	public SeqIterator(ISeq<T> source) {
		this.source=source;
	}

	@Override
	public boolean hasNext() {
		return source!=null;
	}

	@Override
	public T next() {
		ISeq<T> source=this.source;
		T value=source.first();
		this.source=source.next();
		return value;
	}

}
