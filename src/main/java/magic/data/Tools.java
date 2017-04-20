package magic.data;

import java.util.ArrayList;
import java.util.Iterator;

import magic.data.impl.ListIndexSeq;

public class Tools {

	public static <T> ISeq<T> seq(Iterator<T> iterator) {
		if (!iterator.hasNext()) return null;
		ArrayList<T> list=new ArrayList<T>();
		do {
			list.add(iterator.next());
		} while (iterator.hasNext());

		return new ListIndexSeq<T>(list);
	}

}
