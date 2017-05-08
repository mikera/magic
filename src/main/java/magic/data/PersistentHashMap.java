package magic.data;

import java.io.ObjectStreamException;
import java.util.Iterator;
import java.util.Map;

import magic.RT;
import magic.data.impl.BasePersistentSet;
import magic.data.impl.KeySetWrapper;
import magic.data.impl.ValueCollectionWrapper;

/**
 * Persistent HashMap implementation, inspired by Clojure's
 * persistent hash map data structures.
 * 
 * @author Mike Anderson
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public final class PersistentHashMap<K,V> extends APersistentMap<K,V> {
	private static final long serialVersionUID = -6862000512238861885L;

	/**
	 * SHIFT_AMOUNT controls the maximum branching factor.
	 * 
	 * Valid values are 2 (x4) through to 5 bits (x32 branching). 4 seems to be about the sweet spot.
	 */
	private static final int SHIFT_AMOUNT=5;
	private static final int LOW_MASK=(1<<SHIFT_AMOUNT)-1;
	private static final int DATA_SIZE=1<<SHIFT_AMOUNT;
	
	private final PHMNode<K,V> root;

	@SuppressWarnings({ "rawtypes" })
	private static final PHMNode<?,?> EMPTY_NODE_LIST=new PHMNullList();

	@SuppressWarnings("rawtypes")
	public static final PersistentHashMap<?,?> EMPTY=new PersistentHashMap();
	
	
	@SuppressWarnings("unchecked")
	public PersistentHashMap() {
		this((PHMNode<K,V>)EMPTY_NODE_LIST);
	}
	
	@SuppressWarnings("unchecked")
	public PersistentHashMap(PHMNode<K,V> newRoot) {
		if (newRoot==null) newRoot=(PHMNode<K,V>)EMPTY_NODE_LIST;
		root=newRoot;
	}
	
	@SuppressWarnings("unchecked")
	public static<K,V> PersistentHashMap<K,V> create() {
		return (PersistentHashMap<K, V>) PersistentHashMap.EMPTY;
	}
	
	public static<K,V> PersistentHashMap<K,V> create(K key, V value) {
		return new PersistentHashMap<K, V>(new PHMEntry<K, V>(key,value));
	}
	
	@SuppressWarnings("unchecked")
	public static<K,V> PersistentHashMap<K,V> create(Map<K,V> values) {
		PersistentHashMap<K,V> pm=(PersistentHashMap<K, V>) PersistentHashMap.EMPTY;
		for (Map.Entry<K,V> ent: values.entrySet()) {
			pm=pm.assoc(ent.getKey(),ent.getValue());
		}
		return pm;
	}

	public static <K,V> int countEntries(PHMNode<K,V> node) {
		if (node==null) return 0;
		return node.size();
	}
	
	private abstract static class PHMNode<K,V> extends APersistentObject {
		private static final long serialVersionUID = -4378011224932646278L;

		/**
		 * Removes key from PHMNode, returning a modified HashNode
		 * 
		 * @param key
		 * @return Modified PHMNode, the same PHMNode if key not found, or null if all data deleted
		 */
		protected abstract PHMNode<K,V> delete(K key, int hash);

		/**
		 * Returns a new PHMNode including the given (key,value) pair
		 * 
		 * @param key
		 * @param value
		 * @param hash
		 * @param shift
		 * @return
		 */
		protected abstract PHMNode<K,V> include(K key, V value, int hash, int shift);
		
		/**
		 * Returns the entry for the given key value, or null if not found
		 * 
		 * @param key
		 * @param hash Hash of the key, must be provided
		 * @return
		 */
		protected abstract PHMEntry<K,V> getEntry(K key, int hash);
		
		/**
		 * Returns the entry for the given key value, or null if not found
		 * 
		 * @param key
		 * @return
		 */
		protected PHMEntry<K,V> getEntry(K key) {
			return getEntry(key,key.hashCode());
		}
		
		/**
		 * Finds the next entry in the PHMNode map, or null if not found
		 * Updates the given PHMEntrySetIterator
		 * 
		 * @param it PHMEntrySetIterator to be updated
		 * @return the next entry, or null if none remaining
		 */
		protected abstract PHMEntry<K,V> findNext(PHMEntrySetIterator<K,V> it);
		
		/**
		 * Returns the size of the PHMNode, i.e. the total number of distinct entries
		 * @return
		 */
		protected abstract int size();
		
		/**
		 * Determine if the PHMNode is a leaf node (i.e. all entries have the same hash value)
		 * Used to determine how the nodes can be re-used
		 * 
		 * @return true if leaf node, false otherwise
		 */
		protected abstract boolean isLeaf();
		
		/**
		 * Determine if the PHMNode contains a given key
		 * 
		 * @return true if key is present, false otherwise
		 */
		public final boolean containsKey(K key) {
			return getEntry(key)!=null;
		}

		/**
		 * Testing function to validate internal structure of PHMNode
		 */
		@Override
		public abstract void validate();
	}
	
	/**
	 * Represents a full node with DATA_SIZE non-null elements
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static final class PHMFullNode<K,V> extends PHMNode<K,V> {
		private static final long serialVersionUID = 5910832730804486676L;
				
		private final PHMNode<K,V>[] data;
		private final int shift;
		private final int count;	
		
		protected PHMFullNode(PHMNode<K,V>[] newData, int newShift) {
			data=newData;
			shift=newShift;
			count=countEntries();
		}
		
		private static final int slotFromHash(int hash, int shift) {
			return (hash>>>shift)&LOW_MASK;
		}

		@Override
		protected PHMNode<K, V> delete(K key, int hash) {
			int slot=slotFromHash(hash,shift);
			PHMNode<K,V> n=data[slot];
			PHMNode<K,V> dn=n.delete(key, hash);
			if (dn==null) return remove(slot);
			if (dn==n) return this;
			return replace(slot,dn);
		}
		
		@SuppressWarnings("unchecked")
		protected PHMNode<K,V> remove(int i) {
			PHMNode<K,V>[] newdata=new PHMNode[DATA_SIZE-1];
			System.arraycopy(data, 0, newdata, 0, i);
			System.arraycopy(data, i+1, newdata, i, DATA_SIZE-i-1);
			return new PHMBitMapNode<K, V>(newdata,shift,0xFFFFFFFF&(~(1<<i)));
		}
		
		@SuppressWarnings("unchecked")
		protected PHMNode<K, V> replace(int i, PHMNode<K,V> node) {
			PHMNode<K,V>[] newData=new PHMNode[DATA_SIZE];
			System.arraycopy(data, 0, newData, 0, DATA_SIZE);
			newData[i]=node;
			return new PHMFullNode<K, V>(newData,shift);
		}
		
		@Override
		protected PHMEntry<K, V> findNext(PHMEntrySetIterator<K, V> it) {
			int i=slotFromHash(it.position,shift);
			PHMNode<K,V> n=data[i];
			if (n!=null) {
				PHMEntry<K, V> ent=n.findNext(it);
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
		protected PHMEntry<K, V> getEntry(K key, int hash) {
			int i=slotFromHash(hash,shift);
			PHMNode<K,V> n=data[i];
			return n.getEntry(key,hash);
		}

		@Override
		protected PHMNode<K, V> include(K key, V value, int hash, int shift) {
			int i=slotFromHash(hash,shift);
			PHMNode<K,V> n=data[i];
			PHMNode<K, V> dn=n.include(key, value, hash, shift+SHIFT_AMOUNT);
			if (dn==n) return this;
			return replace(i,dn);
		}
		
		/*
		@SuppressWarnings("unchecked")
		protected static <K,V> PHMFullNode<K,V> concat(PHMNode a, int ha, PHMNode b, int hb, int shift) {
			PHMNode<K,V>[] nodes=new PHMNode[DATA_SIZE];
			int ai=slotFromHash(ha,shift);
			int bi=slotFromHash(hb,shift);
			if (ai!=bi) {
				nodes[ai]=a;
				nodes[bi]=b;
			} else {
				nodes[ai]=concat(a,ha,b,hb,shift+SHIFT_AMOUNT);
			}
			PHMFullNode<K,V> fn=new PHMFullNode(nodes,shift);
			return fn;
		}
		*/

		private int countEntries() {
			int res=0;
			for (int i=0; i<data.length; i++) {
				PHMNode<K,V> n=data[i];
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
				PHMNode<K,V> n=data[i];
				count+=n.size();
				if (n instanceof PHMFullNode<?,?>) {
					PHMFullNode<K,V> pfn=(PHMFullNode<K,V>)n;
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
	 * @param <K>
	 * @param <V>
	 */
	public static final class PHMBitMapNode<K,V> extends PHMNode<K,V> {
		private static final long serialVersionUID = -4936128089990848344L;
		
		
		private final PHMNode<K,V>[] data;
		private final int shift;
		private final int count;
		private final int bitmap; // bitmap indicating which slots are present in data array
		
		
		private PHMBitMapNode(PHMNode<K,V>[] newData, int newShift, int newBitmap) {
			data=newData;
			shift=newShift;
			bitmap=newBitmap;
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
			return indexFromSlot(slotFromHash(hash,shift),bitmap);
		}
		
		private final int slotFromIndex(int index) {
			int v=bitmap;
			int m=Bits.lowestSetBit(v);
			while ((index--)>0) {
				v=v&(~m);
				m=Bits.lowestSetBit(v);
			}
			return Integer.bitCount(m-1);
		}

		@Override
		protected PHMNode<K, V> delete(K key, int hash) {
			int i=indexFromHash(hash,shift);
			if (i>=data.length) return this; // needed in case slot not present in current node
			PHMNode<K,V> n=data[i];
			PHMNode<K,V> dn=n.delete(key, hash);
			if (dn==n) return this;
			if (dn==null) {
				return remove(i);
			}
			return replace(i,dn);
		}
		
		@SuppressWarnings("unchecked")
		private PHMNode<K, V> remove(int i) {
			if (data.length==1) return null;
			if (data.length==2) {
				// only return the node if it is a leaf node (otherwise shift levels are disrupted....
				PHMNode<K,V> node=data[1-i];
				if (node.isLeaf()) return node; 
			}
			PHMNode<K,V>[] newData=new PHMNode[data.length-1];
			System.arraycopy(data, 0, newData, 0, i);
			System.arraycopy(data, i+1, newData, i, data.length-i-1);
			return new PHMBitMapNode<K, V>(newData,shift,bitmap&(~(1<<slotFromIndex(i))));
		}
		
		@SuppressWarnings("unchecked")
		protected PHMNode<K, V> replace(int i, PHMNode<K,V> node) {
			PHMNode<K,V>[] newData=new PHMNode[data.length];
			System.arraycopy(data, 0, newData, 0, data.length);
			newData[i]=node;
			return new PHMBitMapNode<K, V>(newData,shift,bitmap);
		}
		
		@Override
		protected PHMEntry<K, V> findNext(PHMEntrySetIterator<K, V> it) {
			// note ugly but fast hack: we store index rather than slot in it.position for bitmap nodes
			int i=slotFromHash(it.position,shift);
			PHMNode<K,V> n=data[i];
			PHMEntry<K, V> ent=n.findNext(it);
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
		protected PHMEntry<K, V> getEntry(K key, int hash) {
			int i=indexFromHash(hash,shift);
			if (i>=data.length) return null;
			PHMNode<K,V> n=data[i];
			if (n!=null) return n.getEntry(key,hash);
			return null;
		}

		@Override
		protected PHMNode<K, V> include(K key, V value, int hash, int shift) {
			int s=slotFromHash(hash,shift);
			int i=indexFromSlot(s,bitmap);
			if (((1<<s)&bitmap)==0) {
				return insertSlot(i,s,new PHMEntry<K, V>(key,value));
			}
			PHMNode<K,V> n=data[i];
			return replace(i,n.include(key, value, hash, shift+SHIFT_AMOUNT));
		}
		
		@SuppressWarnings("unchecked")
		protected PHMNode<K, V> insertSlot(int i, int s, PHMNode<K,V> node) {
			PHMNode<K,V>[] newData=new PHMNode[data.length+1];
			System.arraycopy(data, 0, newData, 0, i);
			System.arraycopy(data, i, newData, i+1, data.length-i);
			newData[i]=node;
			if (data.length==31) {
				return new PHMFullNode<K, V>(newData,shift);
			}
			return new PHMBitMapNode<K, V>(newData,shift,bitmap|(1<<s));
		}
		
		
		@SuppressWarnings("unchecked")
		protected static <K,V> PHMBitMapNode<K,V> concat(PHMNode<K,V> a, int ha, PHMNode<K,V> b, int hb, int shift) {
			PHMNode<K,V>[] nodes;
			int sa=slotFromHash(ha,shift);
			int sb=slotFromHash(hb,shift);
			int bitmap=(1<<sa)|(1<<sb);
			if (sa!=sb) {
				nodes=new PHMNode[2];
				int ia=indexFromSlot(sa,bitmap);
				nodes[ia]=a;
				nodes[1-ia]=b;
			} else {
				nodes=new PHMNode[1];
				nodes[0]=concat(a,ha,b,hb,shift+SHIFT_AMOUNT);
			}
			PHMBitMapNode<K,V> fn=new PHMBitMapNode<K, V>(nodes,shift,bitmap);
			return fn;
		}

		private int countEntries() {
			int res=0;
			for (int i=0; i<data.length; i++) {
				PHMNode<K,V> n=data[i];
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
			if (data.length!=Integer.bitCount(bitmap)) throw new Error();
			int count=0;
			for (int i=0; i<data.length; i++) {
				if (i!=indexFromSlot(slotFromIndex(i),bitmap)) throw new Error();
				PHMNode<K,V> n=data[i];
				count+=n.size();
				if (n instanceof PHMFullNode<?,?>) {
					PHMFullNode<K,V> pfn=(PHMFullNode<K,V>)n;
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
	 * @param <K>
	 * @param <V>
	 */
	private static final class PHMNullList<K,V> extends PHMNode<K,V> {
		private static final long serialVersionUID = 1677618725079327002L;

		
		@Override
		protected PHMNode<K, V> delete(K key, int hash) {
			return this;
		}

		@Override
		protected PHMEntry<K, V> findNext(PHMEntrySetIterator<K, V> it) {
			return null;
		}

		@Override
		protected PHMEntry<K, V> getEntry(K key, int hash) {
			return null;
		}

		@Override
		protected PHMNode<K, V> include(K key, V value, int hash, int shift) {
			return new PHMEntry<K, V>(key,value);
		}

		@Override
		protected int size() {
			return 0;
		}

		@Override
		public void validate() {
			// TODO; validation
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
	
	
	private static final class PHMCollisionList<K,V> extends PHMNode<K,V> {
		private static final long serialVersionUID = -2314559707707984910L;
	
		
		private final PHMEntry<K,V>[] entries;
		private final int hashCode;
		
		public PHMCollisionList(PHMEntry<K,V>[] list, int hash) {
			entries=list;
			hashCode=hash;
		}

		@Override
		protected PHMEntry<K, V> getEntry(K key, int hash) {
			if (hash!=hashCode) return null;
			return getEntry(key);
		}
		
		@Override
		protected PHMEntry<K, V> getEntry(K key) {
			for (PHMEntry<K,V> ent : entries) {
				if (ent.matches(key)) return ent;
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected PHMNode<K, V> include(K key, V value, int hash, int shift) {
			if (hashCode!=hash) {
				return PHMBitMapNode.concat(this,hashCode,new PHMEntry<K, V>(key,value),hash,shift);

			}
			
			int pos=-1;
			for (int i=0; i<entries.length; i++) {
				PHMEntry<K,V> ent=entries[i];
				if (ent.matches(key)) {
					if (ent.matchesValue(value)) return this;
					pos=i;
					break;
				}
			}
			int olen=entries.length;
			int nlen=olen+( (pos>=0)?0:1 );
			PHMEntry<K,V>[] ndata=new PHMEntry[nlen];
			System.arraycopy(entries, 0, ndata, 0, entries.length);
			if (pos>=0) {
				ndata[pos]=new PHMEntry<K, V>(key,value);
			} else {
				ndata[olen]=new PHMEntry<K, V>(key,value);
			}
			return new PHMCollisionList<K, V>(ndata,hash);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected PHMNode<K, V> delete(K key, int hash) {
			if (hash!=hashCode) return this;
			
			// search for matching entry
			int pos=-1;
			int len=entries.length;
			for (int i=0; i<len; i++) {
				PHMEntry<K,V> ent=entries[i];
				if (ent.matches(key)) {
					pos=i;
				}
			}
			
			if (pos<0) return this; // not found
			if (len==2) {
				return entries[1-pos]; // return other entry
			}
			PHMEntry<K,V>[] ndata=new PHMEntry[len-1];
			System.arraycopy(entries,0,ndata,0,pos);
			System.arraycopy(entries,pos+1,ndata,pos,len-pos-1);
			return new PHMCollisionList<K, V>(ndata,hash);
		}

		@Override
		protected int size() {
			return entries.length;
		}

		@Override
		protected PHMEntry<K, V> findNext(PHMEntrySetIterator<K, V> it) {
			if (it.index>=entries.length) return null;
			
			return entries[it.index++];
		}

		@Override
		public void validate() {
			for (PHMEntry<K,V> e:entries) {
				e.validate();
				if (hashCode!=e.key.hashCode()) throw new Error();
			}
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}
	}
	
	/**
	 * Represents a single PersistentHashMap entry
	 * 
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static final class PHMEntry<K,V> extends PHMNode<K,V> implements Map.Entry<K, V> {
		private static final long serialVersionUID = -4668010646096033269L;
			
		private final K key;
		private final V value;		
		
		@Override
		public K getKey() {
			return key;
		}
		
		@Override
		public V getValue() {
			return value;
		}
				
		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}	
		
		public PHMEntry(K k, V v) {
			key=k;
			value=v;
		}
		
		public boolean matches(K key) {
			return this.key.equals(key);
		}
		
		public boolean matchesValue(V value) {
			return this.value==value;
		}
		
		@Override
		protected PHMEntry<K, V> getEntry(K key) {
			if (matches(key)) return this;
			return null;
		}
		
		@Override
		protected PHMEntry<K, V> getEntry(K key, int hash) {
			return getEntry(key);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected PHMNode<K, V> include(K newkey, V value, int hash, int shift) {
			if (newkey.equals(this.key)) {
				// replacement case
				if (!matchesValue(value)) return new PHMEntry<K, V>(newkey,value);
				return this;
			}

			int hashCode=this.key.hashCode();
			if (hash==hashCode) return new PHMCollisionList<K, V>(
					new PHMEntry[] {
							this,
							new PHMEntry<K, V>(newkey,value)},
					hash);
			
			return PHMBitMapNode.concat(this,hashCode,new PHMEntry<K, V>(newkey,value),hash,shift);
		}
		
		@Override
		protected PHMNode<K, V> delete(K k, int hash) {
			if (k.equals(key)) return null;
			return this;
		}
		
		@Override
		protected int size() {
			return 1;
		}

		@Override
		protected PHMEntry<K,V> findNext(PHMEntrySetIterator<K, V> it) {
			if (it.index>0) {
				return null;
			}
			it.index=1;
			return this;
		}

		@Override
		public void validate() {
			if (key==null) throw new Error();
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}
		
		// toString() consistent with java.util.AbstractMap
		@Override
		public String toString() {
			return String.valueOf(key)+'='+String.valueOf(value);
		}
	}
	
	/**
	 * EntrySet implementation
	 */
	protected final class PHMEntrySet extends BasePersistentSet<Map.Entry<K,V>> {
		private static final long serialVersionUID = -3437346777467759443L;

		@Override
		public int size() {
			return PersistentHashMap.this.size();
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry<?,?>)) return false;
			Map.Entry<?,?> ent=(Map.Entry<?,?>)o;
			PHMEntry<K,V> pe=PersistentHashMap.this.getEntry((K)ent.getKey());
			if (pe==null) return false;
			return RT.equals(pe.value, ent.getValue());
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new PHMEntrySetIterator<K, V>(PersistentHashMap.this);
		}

		@Override
		public APersistentSet<Map.Entry<K, V>> include(
				Map.Entry<K, V> value) {
			return Sets.create(this).include(value);
		}
	}
	
	
	/**
	 * Entry set iterator
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static class PHMEntrySetIterator<K,V> implements Iterator<Map.Entry<K,V>> {
		public PHMNode<K,V> root;
		public PHMEntry<K,V> next;
		public int position=0;
		public int index=0;
		
		private PHMEntrySetIterator(PersistentHashMap<K,V> phm) {
			root=phm.root;
			findNext();
		}

		@Override
		public boolean hasNext() {
			return (next!=null);
		}

		@Override
		public PHMEntry<K, V> next() {
			PHMEntry<K, V> result=next;
			findNext();
			return result;
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
	 *  IPersistentMap methods
	 */

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		return root.containsKey((K)key);
	}

	@Override
	public APersistentSet<java.util.Map.Entry<K, V>> entrySet() {
		return new PHMEntrySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		PHMEntry<K,V> entry=root.getEntry((K)key);
		if (entry!=null) return entry.getValue();
		return null;
	}
	
	public PHMEntry<K,V> getEntry(K key) {
		return root.getEntry(key);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map.Entry<K,V> getMapEntry(Object key) {
		return getEntry((K)key);
	}

	@Override
	public APersistentSet<K> keySet() {
		return new KeySetWrapper<K, V>(entrySet());
	}

	@Override
	public int size() {
		return root.size();
	}

	@Override
	public APersistentCollection<V> values() {
		return new ValueCollectionWrapper<K, V>(entrySet());
	}

	@Override
	public PersistentHashMap<K, V> assoc(K key, V value) {
		PHMNode<K,V> newRoot=root.include(key, value,key.hashCode(),0);
		if (root==newRoot) return this;
		return new PersistentHashMap<K, V>(newRoot);
	}
	
	@Override
	public APersistentMap<K, V> include(Map<K, V> values) {
		if (values instanceof PersistentHashMap<?,?>) {
			return include((PersistentHashMap<K,V>)values);
		}
		
		APersistentMap<K, V> pm=this;
		for (Map.Entry<K, V> entry:values.entrySet()) {
			pm=pm.assoc(entry.getKey(),entry.getValue());
		}
		return pm;
	}
	
	public APersistentMap<K, V> include(PersistentHashMap<K, V> values) {
		// TODO: Consider fast node-level merging implementation
		PersistentHashMap<K, V> pm=this;
		for (Map.Entry<K, V> entry:values.entrySet()) {
			pm=pm.assoc(entry.getKey(),entry.getValue());
		}
		return pm;
	}

	@Override
	public APersistentMap<K, V> dissoc(K key) {
		PHMNode<K,V> newRoot=root.delete(key,key.hashCode());
		if (root==newRoot) return this;
		return new PersistentHashMap<K, V>(newRoot);
	}
	
	@Override
	public void validate() {
		super.validate();
		root.validate();
	}
	
	@Override
	public boolean allowsNullKey() {
		return false;
	}
}
