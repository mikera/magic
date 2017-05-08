package magic.data;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import magic.RT;
import magic.data.impl.BasePersistentSet;


/**
 * Persistent HashSet implementation, inspired by Clojure's
 * persistent hash Set data structures.
 * 
 * @author Mike Anderson
 * @param <T> Type of objects stored in the set
 */

public final class PersistentHashSet<T> extends BasePersistentSet<T> {
	private static final long serialVersionUID = -418123646874765874L;

	/**
	 * SHIFT_AMOUNT controls the maximum branching factor.
	 * 
	 * Valid values are 2 (x4) through to 5 bits (x32 branching). 4 seems to be about the sweet spot.
	 */
	public static final int SHIFT_AMOUNT=5;
	public static final int LOW_MASK=(1<<SHIFT_AMOUNT)-1;
	public static final int DATA_SIZE=1<<SHIFT_AMOUNT;
	
	private final PHSNode<T> root;
	

	@SuppressWarnings("rawtypes")
	private static final PHSNode<?> EMPTY_NODE_LIST=new PHSNullList();
	
	@SuppressWarnings("unchecked")
	public PersistentHashSet() {
		this((PHSNode<T>) EMPTY_NODE_LIST);
	}
	
	@SuppressWarnings("unchecked")
	private PersistentHashSet(PHSNode<T> newRoot) {
		if (newRoot==null) newRoot=(PHSNode<T>) EMPTY_NODE_LIST;
		root=newRoot;
	}

	public static<T> PersistentHashSet<T> createFromSet(Set<T> values) {
		PersistentHashSet<T> pm=new PersistentHashSet<T>();
		if (values==null) return pm;
		for (T ent: values) {
			pm=pm.include(ent);
		}
		return pm;
	}
	
	public static<T> PersistentHashSet<T> createFromIterator(Iterator<T> iterator) {
		PersistentHashSet<T> pm=new PersistentHashSet<T>();
		while(iterator.hasNext()) {
			pm=pm.include(iterator.next());
		}
		return pm;
	}
	
	public static <T> PersistentHashSet<T> create(T[] values) {
		PersistentHashSet<T> pm=new PersistentHashSet<T>();
		for (int i=0; i<values.length; i++) {
			pm=pm.include(values[i]);
		}
		return pm;
	}
	
	public static <T> PersistentHashSet<T> create() {
		PersistentHashSet<T> pm=new PersistentHashSet<T>();
		return pm;
	}
	
	public static <T> PersistentHashSet<T> create(Collection<T> values) {
		return createFromIterator(values.iterator());
	}
	
	public static <T> PersistentHashSet<T> createSingleValueSet(T value) {
		return new PersistentHashSet<T>(new PHSEntry<T>(value));
	}

	public static <T> int countEntries(PHSNode<T> node) {
		if (node==null) return 0;
		return node.size();
	}
	
	private abstract static class PHSNode<T> extends APersistentObject {
		private static final long serialVersionUID = -4378011224932646278L;

		/**
		 * Removes key from PHSNode, returning a modified HashNode
		 * 
		 * @param key
		 * @return Modified PHSNode, the same PHSNode if key not found, or null if all data deleted
		 */
		protected abstract PHSNode<T> delete(T key, int hash);

		/**
		 * Returns a new PHSNode including the given key
		 * 
		 * @param key
		 * @param localKey
		 * @param hash
		 * @param shift
		 * @return
		 */
		protected abstract PHSNode<T> include(T key, int hash, int shift);
		
		protected abstract PHSNode<T> include(PHSEntry<T> entry, int hash, int shift);

		
		/**
		 * Returns the entry for the given key, or null if not found
		 * 
		 * @param key
		 * @param hash Hash of the key, must be provided
		 * @return
		 */
		protected abstract PHSEntry<T> getEntry(T key, int hash);
		
		/**
		 * Returns the entry for the given key, or null if not found
		 * 
		 * @param key
		 * @return
		 */
		protected PHSEntry<T> getEntry(T key) {
			return getEntry(key,calcHash(key));
		}
		
		/**
		 * Finds the next entry in the PHSNode Set, or null if not found
		 * Updates the given PHSIterator
		 * 
		 * @param it PHSIterator to be updated
		 * @return the next entry, or null if none remaining
		 */
		protected abstract PHSEntry<T> findNext(PHSIterator<T> it);
		
		/**
		 * Returns the size of the PHSNode, i.e. the total number of distinct entries
		 * @return
		 */
		protected abstract int size();
		
		/**
		 * Determine if the PHSNode is a leaf node (i.e. all entries have the same hash value)
		 * Used to determine how the nodes can be re-used
		 * 
		 * @return true if leaf node, false otherwise
		 */
		protected abstract boolean isLeaf();
		
		/**
		 * Determine if the PHSNode contains a given key
		 * 
		 * @return true if key is present, false otherwise
		 */
		public final boolean containsKey(T key) {
			return getEntry(key)!=null;
		}

		/**
		 * Testing function to validate internal structure of PHSNode
		 */
		@Override
		public abstract void validate();
	}
	
	/**
	 * Represents a full node with DATA_SIZE non-null elements
	 * @author Mike
	 */
	private static final class PHSFullNode<T> extends PHSNode<T> {
		private static final long serialVersionUID = 5910832730804486676L;
		
		
		private final PHSNode<T>[] data;
		private final int shift;
		private final int count;
		
		
		protected PHSFullNode(PHSNode<T>[] newData, int newShift) {
			data=newData;
			shift=newShift;
			count=countEntries();
		}
		
		private static final int slotFromHash(int hash, int shift) {
			return (hash>>>shift)&LOW_MASK;
		}

		@Override
		protected PHSNode<T> delete(T key, int hash) {
			int slot=slotFromHash(hash,shift);
			PHSNode<T> n=data[slot];
			PHSNode<T> dn=n.delete(key, hash);
			if (dn==null) return remove(slot);
			if (dn==n) return this;
			return replace(slot,dn);
		}
		
		@SuppressWarnings("unchecked")
		protected PHSNode<T> remove(int i) {
			PHSNode<T>[] newdata=new PHSNode[DATA_SIZE-1];
			System.arraycopy(data, 0, newdata, 0, i);
			System.arraycopy(data, i+1, newdata, i, DATA_SIZE-i-1);
			return new PHSBitSetNode<T>(newdata,shift,0xFFFFFFFF&(~(1<<i)));
		}
		
		@SuppressWarnings("unchecked")
		protected PHSNode<T> replace(int i, PHSNode<T> node) {
			PHSNode<T>[] newData=new PHSNode[DATA_SIZE];
			System.arraycopy(data, 0, newData, 0, DATA_SIZE);
			newData[i]=node;
			return new PHSFullNode<T>(newData,shift);
		}
		
		@Override
		protected PHSEntry<T> findNext(PHSIterator<T> it) {
			int i=slotFromHash(it.position,shift);
			PHSNode<T> n=data[i];
			if (n!=null) {
				PHSEntry<T> ent=n.findNext(it);
				if (ent!=null) return ent;
			}
			i++;
			while(i<DATA_SIZE) {
				n=data[i];
				if (n!=null) {
					it.position=(it.position&((1<<shift)-1)) | ((i<<shift));
					it.index=0;
					return n.findNext(it);
				}
				i++;
			}
			return null;
		}

		@Override
		protected PHSEntry<T> getEntry(T key, int hash) {
			int i=slotFromHash(hash,shift);
			PHSNode<T> n=data[i];
			return n.getEntry(key,hash);
		}

		@Override
		protected PHSNode<T> include(T key, int hash, int shift) {
			int i=slotFromHash(hash,shift);
			PHSNode<T> n=data[i];
			PHSNode<T> dn=n.include(key, hash, shift+SHIFT_AMOUNT);
			if (dn==n) return this;
			return replace(i,dn);
		}
		
		@Override
		protected PHSNode<T> include(PHSEntry<T> entry, int hash, int shift) {
			int i=slotFromHash(hash,shift);
			PHSNode<T> n=data[i];
			PHSNode<T> dn=n.include(entry, hash, shift+SHIFT_AMOUNT);
			if (dn==n) return this;
			return replace(i,dn);
		}
		
		private int countEntries() {
			int res=0;
			for (int i=0; i<data.length; i++) {
				PHSNode<T> n=data[i];
				res+=n.size();
			}
			return res;
		}
		
		@Override
		protected int size() {
			return count;
		}

		@Override
		public void validate() {
			int count=0;
			for (int i=0; i<DATA_SIZE; i++) {
				PHSNode<T> n=data[i];
				count+=n.size();
				if (n instanceof PHSFullNode<?>) {
					PHSFullNode<T> pfn=(PHSFullNode<T>)n;
					if (pfn.shift!=(this.shift+SHIFT_AMOUNT)) throw new Error();
				}
				n.validate();
			}
			if (count!=size()) throw new Error();
		}

		@Override
		protected boolean isLeaf() {
			return false;
		}	
	}
	
	/**
	 * Represents a bitmapped node with 1 to DATA_SIZE-1 branches
	 * 
	 * Inspired by Clojure's persistent data structures
	 * 
	 * @author Mike
	 *
	 */
	public static final class PHSBitSetNode<T> extends PHSNode<T> {
		private static final long serialVersionUID = -4936128089990848344L;
		
		
		private final PHSNode<T>[] data;
		private final int shift;
		private final int count;
		private final int bitSet; // bitSet indicating which slots are present in data array
		
		
		private PHSBitSetNode(PHSNode<T>[] newData, int newShift, int newBitSet) {
			data=newData;
			shift=newShift;
			bitSet=newBitSet;
			count=countEntries();
		}
		
		public static final int indexFromSlot(int slot, int bm) {
			int mask = (1<<slot) - 1;
			return Integer.bitCount( bm & mask );
		}
		
		public static final int slotFromHash(int hash, int shift) {
			int slot=(hash>>>shift)&LOW_MASK;
			return slot;
		}
		
		private final int indexFromHash(int hash, int shift) {
			return indexFromSlot(slotFromHash(hash,shift),bitSet);
		}
		
		private final int slotFromIndex(int index) {
			int v=bitSet;
			int m=Bits.lowestSetBit(v);
			while ((index--)>0) {
				v=v&(~m);
				m=Bits.lowestSetBit(v);
			}
			return Integer.bitCount(m-1);
		}

		@Override
		protected PHSNode<T> delete(T key, int hash) {
			int i=indexFromHash(hash,shift);
			if (i>=data.length) return this; // needed in case slot not present in current node
			PHSNode<T> n=data[i];
			PHSNode<T> dn=n.delete(key, hash);
			if (dn==n) return this;
			if (dn==null) {
				return remove(i);
			}
			return replace(i,dn);
		}
		
		@SuppressWarnings("unchecked")
		private PHSNode<T> remove(int i) {
			if (data.length==1) return null;
			if (data.length==2) {
				// only return the node if it is a leaf node (otherwise shift levels are disrupted....
				PHSNode<T> node=data[1-i];
				if (node.isLeaf()) return node; 
			}
			PHSNode<T>[] newData=new PHSNode[data.length-1];
			System.arraycopy(data, 0, newData, 0, i);
			System.arraycopy(data, i+1, newData, i, data.length-i-1);
			return new PHSBitSetNode<T>(newData,shift,bitSet&(~(1<<slotFromIndex(i))));
		}
		
		@SuppressWarnings("unchecked")
		protected PHSNode<T> replace(int i, PHSNode<T> node) {
			PHSNode<T>[] newData=new PHSNode[data.length];
			System.arraycopy(data, 0, newData, 0, data.length);
			newData[i]=node;
			return new PHSBitSetNode<T>(newData,shift,bitSet);
		}
		
		@Override
		protected PHSEntry<T> findNext(PHSIterator<T> it) {
			// note ugly but fast hack: we store index rather than slot in it.position for bitSet nodes
			int i=slotFromHash(it.position,shift);
			PHSNode<T> n=data[i];
			PHSEntry<T> ent=n.findNext(it);
			if (ent!=null) return ent;
			i++;
			if(i<data.length) {
				n=data[i];
				// here again we store index rather than slot
				it.position=(it.position&((1<<shift)-1)) | ((i<<shift));
				it.index=0;
				return n.findNext(it);
			}
			return null;
		}

		@Override
		protected PHSEntry<T> getEntry(T key, int hash) {
			int i=indexFromHash(hash,shift);
			if (i>=data.length) return null;
			PHSNode<T> n=data[i];
			if (n!=null) return n.getEntry(key,hash);
			return null;
		}

		@Override
		protected PHSNode<T> include(T key, int hash, int shift) {
			int s=slotFromHash(hash,shift);
			int i=indexFromSlot(s,bitSet);
			if (((1<<s)&bitSet)==0) {
				return insertSlot(i,s,new PHSEntry<T>(key));
			}
			PHSNode<T> n=data[i];
			return replace(i,n.include(key, hash, shift+SHIFT_AMOUNT));
		}
		
		@Override
		protected PHSNode<T> include(PHSEntry<T> entry, int hash, int shift) {
			int s=slotFromHash(hash,shift);
			int i=indexFromSlot(s,bitSet);
			if (((1<<s)&bitSet)==0) {
				return insertSlot(i,s,entry);
			}
			PHSNode<T> n=data[i];
			return replace(i,n.include(entry, hash, shift+SHIFT_AMOUNT));
		}
		
		@SuppressWarnings("unchecked")
		protected PHSNode<T> insertSlot(int i, int s, PHSNode<T> node) {
			PHSNode<T>[] newData=new PHSNode[data.length+1];
			System.arraycopy(data, 0, newData, 0, i);
			System.arraycopy(data, i, newData, i+1, data.length-i);
			newData[i]=node;
			if (data.length==31) {
				return new PHSFullNode<T>(newData,shift);
			}
			return new PHSBitSetNode<T>(newData,shift,bitSet|(1<<s));
		}
		
		
		@SuppressWarnings("unchecked")
		protected static <T> PHSBitSetNode<T> concat(PHSNode<T> a, int ha, PHSNode<T> b, int hb, int shift) {
			PHSNode<T>[] nodes;
			int sa=slotFromHash(ha,shift);
			int sb=slotFromHash(hb,shift);
			int bitSet=(1<<sa)|(1<<sb);
			if (sa!=sb) {
				nodes=new PHSNode[2];
				int ia=indexFromSlot(sa,bitSet);
				nodes[ia]=a;
				nodes[1-ia]=b;
			} else {
				nodes=new PHSNode[1];
				nodes[0]=concat(a,ha,b,hb,shift+SHIFT_AMOUNT);
			}
			PHSBitSetNode<T> fn=new PHSBitSetNode<T>(nodes,shift,bitSet);
			return fn;
		}

		private int countEntries() {
			int res=0;
			for (int i=0; i<data.length; i++) {
				PHSNode<T> n=data[i];
				res+=n.size();
			}
			return res;
		}
		
		@Override
		protected int size() {
			return count;
		}

		@Override
		public void validate() {
			if (data.length!=Integer.bitCount(bitSet)) throw new Error();
			int count=0;
			for (int i=0; i<data.length; i++) {
				if (i!=indexFromSlot(slotFromIndex(i),bitSet)) throw new Error();
				PHSNode<T> n=data[i];
				count+=n.size();
				if (n instanceof PHSFullNode<?>) {
					PHSFullNode<T> pfn=(PHSFullNode<T>)n;
					if (pfn.shift!=(this.shift+SHIFT_AMOUNT)) throw new Error();
				}
				n.validate();
			}
			if (count!=size()) throw new Error();
		}	
		
		@Override
		protected boolean isLeaf() {
			return false;
		}	
	}

	/**
	 * Null list implementation for starting root nodes
	 * @author Mike
	 *
	 */
	private static final class PHSNullList<T> extends PHSNode<T> {
		private static final long serialVersionUID = 1677618725079327002L;

		
		@Override
		protected PHSNode<T> delete(T key, int hash) {
			return this;
		}

		@Override
		protected PHSEntry<T> findNext(PHSIterator<T> it) {
			return null;
		}

		@Override
		protected PHSEntry<T> getEntry(T key, int hash) {
			return null;
		}

		@Override
		protected PHSNode<T> include(T key, int hash, int shift) {
			return new PHSEntry<T>(key);
		}
		
		@Override
		protected PHSNode<T> include(PHSEntry<T> entry, int hash, int shift) {
			return entry;
		}

		@Override
		protected int size() {
			return 0;
		}

		@Override
		public void validate() {
			// TODO: validation
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}	
		
		private Object readResolve() throws ObjectStreamException {
			// needed for deserialisation to the correct static instance
			return EMPTY_NODE_LIST;
		}
	}
	
	
	private static final class PHSCollisionList<T> extends PHSNode<T> {
		private static final long serialVersionUID = -2314559707707984910L;
	
		
		private final PHSEntry<T>[] entries;
		private final int hashCode;
		
		public PHSCollisionList(PHSEntry<T>[] list, int hash) {
			entries=list;
			hashCode=hash;
		}

		@Override
		protected PHSEntry<T> getEntry(T key, int hash) {
			if (hash!=hashCode) return null;
			return getEntry(key);
		}
		
		@Override
		protected PHSEntry<T> getEntry(T key) {
			for (PHSEntry<T> ent : entries) {
				if (ent.matches(key)) return ent;
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected PHSNode<T> include(T key, int hash, int shift) {
			if (hashCode!=hash) {
				return PHSBitSetNode.concat(this,hashCode,new PHSEntry<T>(key),hash,shift);

			}
			
			int pos=-1;
			for (int i=0; i<entries.length; i++) {
				PHSEntry<T> ent=entries[i];
				if (ent.matches(key)) {
					return this;
				}
			}
			int olen=entries.length;
			int nlen=olen+( (pos>=0)?0:1 );
			PHSEntry<T>[] ndata=new PHSEntry[nlen];
			System.arraycopy(entries, 0, ndata, 0, entries.length);
			if (pos>=0) {
				ndata[pos]=new PHSEntry<T>(key);
			} else {
				ndata[olen]=new PHSEntry<T>(key);
			}
			return new PHSCollisionList<T>(ndata,hash);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected PHSNode<T> include(PHSEntry<T> entry, int hash, int shift) {
			if (hashCode!=hash) {
				return PHSBitSetNode.concat(this,hashCode,entry,hash,shift);
			}
			
			T key=entry.localKey;
			int pos=-1;
			for (int i=0; i<entries.length; i++) {
				PHSEntry<T> ent=entries[i];
				if (ent.matches(key)) {
					return this;
				}
			}
			int olen=entries.length;
			int nlen=olen+( (pos>=0)?0:1 );
			PHSEntry<T>[] ndata=new PHSEntry[nlen];
			System.arraycopy(entries, 0, ndata, 0, entries.length);
			if (pos>=0) {
				ndata[pos]=entry;
			} else {
				ndata[olen]=entry;
			}
			return new PHSCollisionList<T>(ndata,hash);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected PHSNode<T> delete(T key, int hash) {
			if (hash!=hashCode) return this;
			
			// search for matching entry
			int pos=-1;
			int len=entries.length;
			for (int i=0; i<len; i++) {
				PHSEntry<T> ent=entries[i];
				if (ent.matches(key)) {
					pos=i;
				}
			}
			
			if (pos<0) return this; // not found
			if (len==2) {
				return entries[1-pos]; // return other entry
			}
			PHSEntry<T>[] ndata=new PHSEntry[len-1];
			System.arraycopy(entries,0,ndata,0,pos);
			System.arraycopy(entries,pos+1,ndata,pos,len-pos-1);
			return new PHSCollisionList<T>(ndata,hash);
		}

		@Override
		protected int size() {
			return entries.length;
		}

		@Override
		protected PHSEntry<T> findNext(PHSIterator<T> it) {
			if (it.index>=entries.length) return null;
			
			return entries[it.index++];
		}

		@Override
		public void validate() {
			for (PHSEntry<T> e:entries) {
				e.validate();
				if (hashCode!=calcHash(e.localKey)) throw new Error();
			}
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}
	}
	
	/**
	 * Represents a single PersistentHashSet entry
	 * 
	 * @author Mike
	 */
	private static final class PHSEntry<T> extends PHSNode<T> {
		private static final long serialVersionUID = -4668010646096033269L;
			
		private final T localKey;
		
		public T getValue() {
			return localKey;
		}
				
		public PHSEntry(T v) {
			localKey=v;
		}
		
		public boolean matches(T key) {
			return RT.equals(localKey,key);
		}
		
		@Override
		protected PHSEntry<T> getEntry(T key) {
			if (matches(key)) return this;
			return null;
		}
		
		@Override
		protected PHSEntry<T> getEntry(T key, int hash) {
			return getEntry(key);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected PHSNode<T> include(T newkey, int hash, int shift) {
			if (matches(newkey)) {
				return this;
			}

			int hashCode=calcHash(localKey);
			if (hash==hashCode) return new PHSCollisionList<T>(
					new PHSEntry[] {
							this,
							new PHSEntry<T>(newkey)},
					hash);
			
			return PHSBitSetNode.concat(this,hashCode,new PHSEntry<T>(newkey),hash,shift);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected PHSNode<T> include(PHSEntry<T> entry, int hash, int shift) {
			T newkey=entry.localKey;
			if (matches(newkey)) {
				return this;
			}

			int hashCode=calcHash(localKey);
			if (hash==hashCode) return new PHSCollisionList<T>(
					new PHSEntry[] {
							this,
							entry},
					hash);
			
			return PHSBitSetNode.concat(this,hashCode,entry,hash,shift);
		
		}
		
		@Override
		protected PHSNode<T> delete(T k, int hash) {
			if (k.equals(localKey)) return null;
			return this;
		}
		
		@Override
		protected int size() {
			return 1;
		}

		@Override
		protected PHSEntry<T> findNext(PHSIterator<T> it) {
			if (it.index>0) {
				return null;
			}
			it.index=1;
			return this;
		}

		@Override
		public void validate() {
			// nothing to do
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}
		
		// toString() consistent with java.util.AbstractSet
		@Override
		public String toString() {
			return String.valueOf(localKey);
		}
	}
	

	
	/**
	 * Set iterator
	 * @author Mike
	 */
	private static class PHSIterator<T> implements Iterator<T> {
		public PHSNode<T> root;
		public PHSEntry<T> next;
		public int position=0;
		public int index=0;
		
		private PHSIterator(PersistentHashSet<T> PHS) {
			root=PHS.root;
			findNext();
		}

		@Override
		public boolean hasNext() {
			return (next!=null);
		}

		@Override
		public T next() {
			PHSEntry<T> result=next;
			findNext();
			return result.getValue();
		}
				
		private void findNext() {
			next=root.findNext(this);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	

	
	/*
	 *  IPersistentSet methods
	 */

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object key) {
		return root.containsKey((T)key);
	}

	
	public PHSEntry<T> getEntry(T key) {
		return root.getEntry(key);
	}


	@Override
	public int size() {
		return root.size();
	}

	@Override
	public PersistentHashSet<T> include(T key) {
		PHSNode<T> newRoot=root.include(key, calcHash(key),0);
		if (root==newRoot) return this;
		return new PersistentHashSet<T>(newRoot);
	}
	
	public static<T> int calcHash(T key) {
		if (key==null) return 0;
		return key.hashCode();
	}
	
	@Override
	public PersistentHashSet<T> includeAll(Collection<T> values) {
		if (values instanceof PersistentHashSet<?>) {
			return include((PersistentHashSet<T>)values);
		}
		
		PersistentHashSet<T> pm=this;
		for (T entry:values) {
			pm=pm.include(entry);
		}
		return pm;
	}
	
	public PersistentHashSet<T> include(PersistentHashSet<T> values) {
		// TODO: Consider fast node-level merging implementation
		PersistentHashSet<T> pm=this;
		PHSIterator<T> it=values.iterator();
		while (it.hasNext()) {
			pm=pm.include(it.next());
		}
		return pm;
	}


	@Override
	public PersistentHashSet<T> exclude(T key) {
		PHSNode<T> newRoot=root.delete(key,calcHash(key));
		if (root==newRoot) return this;
		return new PersistentHashSet<T>(newRoot);
	}

	@Override
	public PHSIterator<T> iterator() {
		return new PHSIterator<T>(this);
	}

	@Override
	public ISeq<T> seq() {
		if (size()==0) return null;
		return Tools.seq(this.iterator());
	}

	public static <T> PersistentHashSet<T> coerce(Set<T> values) {
		if (values instanceof PersistentHashSet<?>) {
			return (PersistentHashSet<T>)values;
		}
		return createFromSet(values);
	}




}
