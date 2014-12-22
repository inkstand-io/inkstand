package li.moskito.inkstand;

/**
 * Base class for checked exception that should require special handling. Recommended use is for business related
 * exception cases.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public class InkstandException extends Exception {

    private static final long serialVersionUID = 7946317132691841679L;

    /**
     * Default constructor. The use is discouraged as it does not provide any meaningful information about the problem
     * that causes the exception.
     */
    public InkstandException() {
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
    public InkstandException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor that may be used to create a root-cause exception.
     *
     * @param message
     *            the message describing the problem causing the exception
     */
    public InkstandException(final String message) {
        super(message);
    }

    /**
     * Constructor that may be used to wrap an existing exception withouth additional high-level information
     *
     * @param cause
     *            the root cause of the exception.
     */
    public InkstandException(final Throwable cause) {
        super(cause);
    }

}
