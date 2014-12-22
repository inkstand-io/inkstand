package li.moskito.inkstand;

/**
 * Base class for unchecked exception to be used when no special handling is required and an upper layer should handle
 * the exception. Recommended use is for technical exceptions cases.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public class InkstandRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -3555224244562029448L;

    /**
     * Default constructor. The use is discouraged as it does not provide any meaningful information about the problem
     * that causes the exception.
     */
    public InkstandRuntimeException() {
        super();
    }

    /**
     * Constructor that may be used to wrap an existing exception
     *
     * @param message
     *            message to indicate the higher-level cause of the exception
     * @param cause
     *            the actual cause of the exception
     */
    public InkstandRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor that may be used to create a root-cause exception.
     *
     * @param message
     *            the message describing the problem causing the exception
     */
    public InkstandRuntimeException(final String message) {
        super(message);
    }

    /**
     * Constructor that may be used to wrap an existing exception withouth additional high-level information
     *
     * @param cause
     *            the root cause of the exception.
     */
    public InkstandRuntimeException(final Throwable cause) {
        super(cause);
    }

}
