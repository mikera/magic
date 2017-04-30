package magic.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import magic.Errors;

public class PersistentList<T> extends APersistentList<T> {
 	private static final long serialVersionUID = -3092361115850584058L;

	public static final APersistentList<?> EMPTY = PersistentList.create(Vectors.emptyVector());
	
	private final APersistentVector<T> vector;
	private final int offset;
	private final int size;
	
	private PersistentList(APersistentVector<T> source, int offset, int size) {
		this.vector=source;
		this.offset=offset;
		this.size=size;	
	}

	
	public static <T> APersistentList<T> create(APersistentVector<T> source, int offset, int size) {
		return new PersistentList<T>(source,offset,size);	
	}
	
	public static <T> APersistentList<T> create(APersistentVector<T> source) {
		return new PersistentList<T>(source,0,source.size());	
	}

	@Override
	public APersistentList<T> concat(IPersistentList<T> values) {
		return create(vector.concat(values),offset,size+values.size());
	}

	@Override
	public APersistentList<T> concat(Collection<T> values) {
		return create(vector.concat(values),offset,size+values.size());
	}

	@Override
	public APersistentList<T> insert(int index, T value) {
		return create(vector.insert(offset+index,value),offset,size+1);
	}

	@Override
	public APersistentList<T> insertAll(int index, Collection<T> values) {
		return create(vector.insertAll(offset+index,values),offset,size+values.size());
	}

	@Override
	public APersistentList<T> insertAll(int index, IPersistentList<T> values) {
		return create(vector.insertAll(offset+index,values),offset,size+values.size());
	}

	@Override
	public APersistentList<T> copyFrom(int index, IPersistentList<T> values, int srcIndex, int length) {
		return create(vector.copyFrom(offset+index,values,srcIndex,length),offset,size);
	}

	@Override
	public APersistentList<T> include(T value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public APersistentList<T> deleteAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public APersistentList<T> deleteRange(int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public APersistentList<T> update(int index, T value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T head() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public APersistentList<T> tail() {
		if (size==0) throw new IllegalArgumentException(Errors.indexOutOfBounds(0));
		return create(vector,offset+1,size-1);
	}

	@Override
	public APersistentList<T> front() {
		return create(vector,offset,size/2);
	}

	@Override
	public APersistentList<T> back() {
		int skip=size/2;
		return create(vector,offset+skip,size-skip);
	}

	@SuppressWarnings("unchecked")
	@Override
	public APersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size)) {
			throw new IndexOutOfBoundsException("from: "+fromIndex+" to: " +toIndex+ " with size: "+size);
		}
		int newSize=fromIndex-toIndex;
		if (newSize==0) return (APersistentList<T>) EMPTY;
		if (newSize<0) {
			throw new IllegalArgumentException("Negative sized subList from: "+fromIndex+" to: " +toIndex);
		}
		if ((fromIndex==0)&&(toIndex==size)) return this;
		return create(vector,offset+fromIndex,newSize);
	}

	@Override
	public T get(int i) {
		if ((i<0)||(i>=size)) throw new IndexOutOfBoundsException(Errors.indexOutOfBounds(i));
		return vector.get(offset+i);
	}

	@Override
	public ISeq<T> seq() {
		if (size==0) return null;
		return null;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, T element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T set(int index, T element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(APersistentVector<T> o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
