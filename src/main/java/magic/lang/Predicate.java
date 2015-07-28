package magic.lang;

public abstract class Predicate<T> implements java.util.function.Predicate<T> {

	public abstract boolean test(T a);
}
