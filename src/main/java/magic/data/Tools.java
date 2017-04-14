package magic.data;

import java.util.ArrayList;
import java.util.Iterator;

import magic.data.impl.ListIndexSeq;

public class Tools {

	public static <T> ISeq<T> seq(Iterator<T> iterator) {
		ArrayList<T> list=new ArrayList<T>();
		while(iterator.hasNext()) {
			list.add(iterator.next());
		}
		return new ListIndexSeq<T>(list);
	}

}
