package magic.data;

import magic.Keywords;

/**
 * Class to represent a metadata declaration as data
 * @author Mike
 *
 */
public class MetaData {

	private IPersistentMap<Keyword,Object> meta;

	public MetaData(IPersistentMap<Keyword,Object> meta) {
		this.meta=meta;
	}
	
	public IPersistentMap<Keyword,Object> meta() {
		return meta;
	}

	@SuppressWarnings("unchecked")
	public static MetaData create(IPersistentMap<?, ?> meta) {
		return new MetaData((IPersistentMap<Keyword, Object>) meta);
	}

	/**
	 * Create a metadata object with a keyword value set to true
	 * @param key
	 * @return
	 */
	public static MetaData create(magic.data.Keyword key) {
		return create(key,Boolean.TRUE);
	}

	public static MetaData create(Keyword key, Object val) {
		return new MetaData(PersistentHashMap.create(key, val));
	}
	
	/**
	 * Creates a metadata object according to the following rule:
	 * - Map -> unchanged
	 * - Keyword -> {:keyword true}
	 * - Any other expression -> {:tag expression}
	 * 
	 * @param node
	 * @return
	 */
	public MetaData interpretMetadata(Object node) {
		if (node instanceof IPersistentMap) return MetaData.create((IPersistentMap<?,?>)node);
		if (node instanceof magic.data.Keyword) return MetaData.create((magic.data.Keyword)node);
		return MetaData.create(Keywords.TAG,node);
	}
}
