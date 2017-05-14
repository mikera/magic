package magic.compiler;

public class SourceInfo {
	private final int column;
	private final String source;
	private final int line;

	private SourceInfo(String source, int line, int pos) {
		this.source=source;
		this.line=line;
		this.column=pos;
	}
	
	public String getSource() {
		return source;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getLinePosition() {
		return column;
	}

	public static SourceInfo create(java.lang.String src, int line, int column) {
		return new SourceInfo(src,line,column);
	}
}
