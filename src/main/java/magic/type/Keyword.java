package magic.type;

public class Keyword extends JavaType<magic.data.Keyword> {

	private Keyword() {
		super(magic.data.Keyword.class);
	}
	
	public static final Keyword INSTANCE=new Keyword();
}
