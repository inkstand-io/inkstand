package li.moskito.inkstand.security;

/**
 * Exception to throw when invalid credentials have been provided but the user itself was correct.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public class InvalidCredentialsException extends AuthenticationException {

    private static final long serialVersionUID = -5630701679325764004L;

    public InvalidCredentialsException(final String userId) {
        super("Invalid credentials", userId);
    }

    public InvalidCredentialsException(final String userId, final Throwable cause) {
        super("Invalid credentials", userId, cause);
    }

}
