package magic.data;

import java.util.Collection;
import java.util.List;

public class Lists {

	public static final APersistentList<?> EMPTY=PersistentList.EMPTY;
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> emptyList() {
		return (APersistentList<T>) EMPTY;
	}

	public static <T> APersistentList<T> create(IPersistentCollection<? extends T> a) {
		return PersistentList.create(Vectors.coerce(a));
	}
	
	public static <T> APersistentList<T> create(APersistentCollection<? extends T> a) {
		return PersistentList.create(Vectors.coerce(a));
	}
	
	public static <T> APersistentList<T> create(APersistentVector<? extends T> a) {
		return PersistentList.wrap(a);
	}

	
	public static <T> APersistentList<T> create(List<? extends T> a) {
		if (a==null) throw new Error("null passed to create!");
		return PersistentList.create(Vectors.coerce(a));
	}

	public static <T> APersistentList<T> coerce(APersistentVector<? extends T> vs) {
		return PersistentList.wrap(vs);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> coerce(Collection<? extends T> a) {
		if (a instanceof APersistentVector<?>) return PersistentList.wrap((APersistentVector<T>)a);
		if (a instanceof APersistentList<?>) return (APersistentList<T>)a;
		return create(Vectors.coerce(a));
	}
	

	public static <T> APersistentList<T> coerce(Iterable<? extends T> it) {
		APersistentVector<T> vs=Vectors.emptyVector();
		for (T v : vs) {
			vs=vs.include(v);
		}
		return Lists.coerce(vs);
	}

	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> cons(T value, APersistentList<? extends T> rest) {
		APersistentList<T> r = (APersistentList<T>) rest;
		return (APersistentList<T>) r.include(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> cons(T a, T b, APersistentList<? extends T> rest) {
		return cons(a, cons(b, (APersistentList<T>) rest));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> cons(T a, T b, T c, APersistentList<? extends T> rest) {
		return cons(a, cons(b, cons(c, (APersistentList<T>) rest)));
	}

	@SuppressWarnings("unchecked")
	public static <R,T> APersistentList<R> of(T... vals) {
		return (APersistentList<R>) wrap(vals);
	}

	@SuppressWarnings("unchecked")
	public static <T> APersistentList<T> wrap(T[] vals) {
		if (vals.length==0) return (APersistentList<T>) PersistentList.EMPTY;
		return PersistentList.wrap(vals);
	}

	public static <T> APersistentList<T> create(T[] rs) {
		return wrap(rs.clone());
	}


}
