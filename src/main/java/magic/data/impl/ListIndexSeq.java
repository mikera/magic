package magic.data.impl;

import java.util.List;

import magic.data.ISeq;

public class ListIndexSeq<T> implements ISeq<T> {

	private List<T> source;
	private int count;
	private int offset;

	public ListIndexSeq(List<T> source, int offset, int count) {
		this.source=source;
		this.count=count;
		this.offset=offset;
	}
	
	public ListIndexSeq(List<T> source) {
		this(source,0,source.size());
	}

	@Override
	public T first() {
		return source.get(offset);
	}

	@Override
	public ISeq<T> next() {
		if (count==1) return null;
		return new ListIndexSeq<T>(source,offset+1,count-1);
	}

}
