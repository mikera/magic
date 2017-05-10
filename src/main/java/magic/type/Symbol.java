package magic.type;

public class Symbol extends JavaType<magic.data.Symbol> {

	private Symbol() {
		super(magic.data.Symbol.class);
	}
	
	public static final Symbol INSTANCE=new Symbol();
}
