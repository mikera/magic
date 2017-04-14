package magic.data.impl;

import java.util.List;

import magic.data.ListFactory;
import magic.data.PersistentList;

/**
 * Persistent List constructed using fixed size blocks represented by other persistent lists
 * @author Mike
 *
 * @param <T>
 */
public final class BlockList<T> extends BasePersistentList<T> {
	private static final long serialVersionUID = 7210896608719053578L;

	protected static final int DEFAULT_SHIFT=ListFactory.TUPLE_BUILD_BITS;
	protected static final int SHIFT_STEP=4;
	protected static final int SHIFT_MASK=(1<<SHIFT_STEP)-1;
	
	private final int shift;
	private final int size;
	private final int offset;
	private final PersistentList<T>[] blocks;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final BlockList<?> EMPTY_BLOCKLIST=new BlockList(ListFactory.NULL_PERSISTENT_LIST_ARRAY,DEFAULT_SHIFT,0,0);
	
	public static <T> BlockList<T> create(List<T> list) {
		return create(list,0,list.size());
	}
	
	public static <T> BlockList<T> coerce(List<T> values) {
		if (values instanceof BlockList<?>) return (BlockList<T>) values;
		return create(values,0,values.size());
	}
	
	public static <T> BlockList<T> create(List<T> list, int fromIndex, int toIndex) {
		int size=toIndex-fromIndex;
		if (size<0) throw new IllegalArgumentException();
		
		int shift=DEFAULT_SHIFT;
		while ((1<<(shift+SHIFT_STEP))<size) {
			shift+=SHIFT_STEP;
		}
		return createLocal(list,fromIndex,toIndex,shift);
	}
	
	public static <T> BlockList<T> create(T[] list, int fromIndex, int toIndex) {
		int size=toIndex-fromIndex;
		if (size<0) throw new IllegalArgumentException();
		
		int shift=DEFAULT_SHIFT;
		while ((1<<(shift+SHIFT_STEP))<size) {
			shift+=SHIFT_STEP;
		}
		return createLocal(list,fromIndex,toIndex,shift);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> BlockList<T> createLocal(T[] list, int fromIndex, int toIndex, int shift) {
		if (shift>DEFAULT_SHIFT) {
			int size=toIndex-fromIndex;
			int numBlocks=numBlocks(size,shift);
		
			PersistentList<T>[] bs=(PersistentList<T>[]) new PersistentList<?>[numBlocks];
			for (int i=0; i<(numBlocks-1); i++) {
				bs[i]=createLocal(
						list,
						fromIndex+(i<<shift), 
						fromIndex+((i+1)<<shift),
						shift-SHIFT_STEP);
			}
			bs[numBlocks-1]=createLocal(
					list,
					fromIndex+((numBlocks-1)<<shift), 
					fromIndex+size,
					shift-SHIFT_STEP);
			
			return new BlockList<T>(bs,shift,size,0);			
		}
		return createLowestLevel(list,fromIndex, toIndex,DEFAULT_SHIFT);
	}
		
	@SuppressWarnings("unchecked")
	private static <T> BlockList<T> createLocal(List<T> list, int fromIndex, int toIndex, int shift) {
		if (shift>DEFAULT_SHIFT) {
			int size=toIndex-fromIndex;
			int numBlocks=numBlocks(size,shift);
		
			PersistentList<T>[] bs=(PersistentList<T>[]) new PersistentList<?>[numBlocks];
			for (int i=0; i<(numBlocks-1); i++) {
				bs[i]=createLocal(
						list,
						fromIndex+(i<<shift), 
						fromIndex+((i+1)<<shift),
						shift-SHIFT_STEP);
			}
			bs[numBlocks-1]=createLocal(
					list,
					fromIndex+((numBlocks-1)<<shift), 
					fromIndex+size,
					shift-SHIFT_STEP);
			
			return new BlockList<T>(bs,shift,size,0);			
		}
		return createLowestLevel(list,fromIndex, toIndex,DEFAULT_SHIFT);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> BlockList<T> createLowestLevel(List<T> list, int fromIndex, int toIndex,int shift) {
		int size=toIndex-fromIndex;
		int numBlocks=numBlocks(size,shift);
	
		PersistentList<T>[] bs=(PersistentList<T>[]) new PersistentList<?>[numBlocks];
		for (int i=0; i<(numBlocks-1); i++) {
			bs[i]=ListFactory.subList(
					list,
					fromIndex+(i<<shift), 
					fromIndex+((i+1)<<shift));
		}
		bs[numBlocks-1]=ListFactory.subList(
				list,
				fromIndex+((numBlocks-1)<<shift), 
				fromIndex+size);
	
		return new BlockList<T>(bs,shift,size,0);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> BlockList<T> createLowestLevel(T[] list, int fromIndex, int toIndex,int shift) {
		int size=toIndex-fromIndex;
		int numBlocks=numBlocks(size,shift);
	
		PersistentList<T>[] bs=(PersistentList<T>[]) new PersistentList<?>[numBlocks];
		for (int i=0; i<(numBlocks-1); i++) {
			bs[i]=ListFactory.createFromArray(
					list,
					fromIndex+(i<<shift), 
					fromIndex+((i+1)<<shift));
		}
		bs[numBlocks-1]=ListFactory.createFromArray(
				list,
				fromIndex+((numBlocks-1)<<shift), 
				fromIndex+size);
	
		return new BlockList<T>(bs,shift,size,0);
	}
	
	private static final int numBlocks(int size, int shift) {
		return 1+((size-1)>>shift);
	}
	
	@SuppressWarnings("unchecked")
	private BlockList(PersistentList<?>[] blocks, int sh, int sz, int off) {
		this.blocks=(PersistentList<T>[]) blocks;
		shift=sh;
		size=sz;
		offset=off;
	}
	
	@Override
	public T get(int i) {
		if ((i<0)||(i>=size)) throw new IndexOutOfBoundsException();
		int pos=i+offset;
		int bi=(pos>>shift);
		int bpos=pos&((1<<shift)-1);
		return blocks[bi].get(bpos);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	private int blockStart(int blockIndex) {
		return blockIndex<<shift;
	}
	
	@Override
	public PersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size)) throw new IndexOutOfBoundsException();
		if ((fromIndex>=toIndex)) {
			if (toIndex==fromIndex) return ListFactory.emptyList();
			throw new IllegalArgumentException();
		}
		if ((fromIndex==0)&&(toIndex==size)) return this;
		
		// see if we can take a subset of a single block
		int fromBlock=(fromIndex+offset)>>shift;
		int toBlock=(toIndex-1+offset)>>shift;
		if ((fromBlock)==(toBlock)) {
			int blockStart=blockStart(fromBlock);
			return blocks[fromBlock].subList(fromIndex+offset-blockStart, toIndex+offset-blockStart);
		}
		
		return subBlockList(fromIndex,toIndex);
	}
	
	@Override
	public int hashCode() {
		if (blocks.length==0) return 0;
		int r=blocks[0].hashCode();
		for (int i=1; i<blocks.length; i++) {
			r=Integer.rotateRight(r,blocks[i].size());
			r^=blocks[i].hashCode();
		}
		return r;
	}
	
	/**
	 * Gets a subList as a BlockList with the same shift
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	private BlockList<T> subBlockList(int fromIndex, int toIndex) {
		return new BlockList<T>(blocks,shift,(toIndex-fromIndex),fromIndex+offset);	
	}

	@Override
	public BlockList<T> conj(T value) {
		// TODO Auto-generated method stub
		return null;
	}

}
