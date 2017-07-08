package magic.data.impl;

import java.util.Iterator;

import magic.RT;
import magic.data.ISeq;

/**
 * Abstract base class for seq implementations
 * 
 * @author Mike
 *
 * @param <T>
 */
public abstract class ASeq<T> implements ISeq<T>, Iterable<T> {
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("(");
		boolean first=true;
		for (T t: this) {
			if (first) {
				first=false;
			} else {
				sb.append(' ');
			}
			sb.append(RT.toString(t));
		}		
		sb.append(')');
		return sb.toString();
	}
	
	@Override
	public Iterator<T> iterator() {
		return new SeqIterator<T>(this);
	}
}
