package li.moskito.inkstand;

/**
 * Base class for unchecked exception to be used when no special handling is required and an upper layer should handle
 * the exception. Recommended use is for technical exceptions cases.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
public class InkstandRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -3555224244562029448L;

    public InkstandRuntimeException() {
        super();
    }

    public InkstandRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InkstandRuntimeException(String message) {
        super(message);
    }

    public InkstandRuntimeException(Throwable cause) {
        super(cause);
    }

}
