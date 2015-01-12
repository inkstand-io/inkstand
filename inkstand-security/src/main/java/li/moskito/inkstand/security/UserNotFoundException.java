package li.moskito.inkstand.security;

/**
 * Exception to throw when a user can not be found or does not exist
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
public class UserNotFoundException extends AuthenticationException {

    private static final long serialVersionUID = -8372014686604125604L;

    public UserNotFoundException(final String userId) {
        super("User not found", userId);
    }

    public UserNotFoundException(final String userId, final Throwable cause) {
        super("User not found", userId, cause);
    }
}
