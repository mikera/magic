package magic.lang;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public class MagicLanguage extends TruffleLanguage<MagicContext> {

	public static final String MIME_TYPE = "application/x-magic";
	
	public static final MagicLanguage INSTANCE = new MagicLanguage();

    /**
     * No instances allowed apart from the {@link #INSTANCE singleton instance}.
     */
	private MagicLanguage() {
    }

	@Override
	protected Object evalInContext(Source arg0, Node arg1, MaterializedFrame arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected boolean isObjectOfLanguage(Object arg0) {
		return true;
	}

	@Override
	protected CallTarget parse(Source arg0, Node arg1, String... arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
    protected SourceSection findSourceLocation(MagicContext context, Object value) {
//        if (value instanceof SLFunction) {
//            SLFunction f = (SLFunction) value;
//            return f.getCallTarget().getRootNode().getSourceSection();
//        }
        return null;
    }


	@Override
	protected MagicContext createContext(com.oracle.truffle.api.TruffleLanguage.Env arg0) {
		return new MagicContext();
	}


	@Override
	protected Object findExportedSymbol(MagicContext arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Object getLanguageGlobal(MagicContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
