package magic.data;

public class ArrayIndexSeq<T> implements ISeq<T> {

	private final T[] data;
	private final int offset;
	private final int size;

	public ArrayIndexSeq(T[] data, int offset, int size) {
		this.data=data;
		this.offset=offset;
		this.size=size;
	}
	
	public static <T> ISeq<T> wrap(T[] data, int offset, int size) {
		if ((offset<0)||(size<0)||(offset+size>data.length)) {
			throw new IndexOutOfBoundsException("Insufficient elements in wrapped array!");
		}
		return new ArrayIndexSeq<T>(data,offset,size);
	}

	@Override
	public T first() {
		return data[offset];
	}

	@Override
	public ISeq<T> next() {
		if (size==1) return null;
		return new ArrayIndexSeq<T>(data,offset+1,size-1);
	}


}
