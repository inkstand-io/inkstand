package io.inkstand.security;

import io.inkstand.InkstandException;

/**
 * Exception that should be thrown when the authentication of a user fails. If there are no further details about the
 * reason for the exception, use the {@link AuthenticationException} directly. Otherwise try to use a more detailed
 * version.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class AuthenticationException extends InkstandException {

    private static final long serialVersionUID = 2358944636079872791L;

    private final String userId;

    public AuthenticationException(final String userId) {
        this("Authentication failed", userId, null);
    }

    public AuthenticationException(final String userId, final Throwable cause) {
        this("Authentication failed", userId, cause);
    }

    public AuthenticationException(final String message, final String userId) {
        this(message, userId, null);
    }

    public AuthenticationException(final String message, final String userId, final Throwable cause) {
        super(message, cause);
        this.userId = userId;
    }

    @Override
    public String getMessage() {
        return getMessage(super.getMessage(), userId);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage(super.getLocalizedMessage(), userId);
    }

    private String getMessage(final String message, final String user) {
        return new StringBuilder(32).append(message).append("[user=").append(user).append(']').toString();
    }

    /**
     * The id of the user that failed to authenticate
     *
     * @return a string representing the id of the user whose authentication failed
     */
    public String getUserId() {
        return userId;
    }

}
