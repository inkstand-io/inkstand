package li.moskito.inkstand;

/**
 * Base class for checked exception that should require special handling. Recommended use is for business related
 * exception cases.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
public class InkstandException extends Exception {

    private static final long serialVersionUID = 7946317132691841679L;

    public InkstandException() {
        super();
    }

    public InkstandException(String message, Throwable cause) {
        super(message, cause);
    }

    public InkstandException(String message) {
        super(message);
    }

    public InkstandException(Throwable cause) {
        super(cause);
    }

}
