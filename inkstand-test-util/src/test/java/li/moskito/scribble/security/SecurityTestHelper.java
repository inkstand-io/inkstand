package li.moskito.scribble.security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

/**
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
public final class SecurityTestHelper {

    private SecurityTestHelper() {
    }

    /**
     * Creates a subject for a user with the given name
     * 
     * @param userName
     *            name of the user
     * @return a subject for the user
     */
    public static Subject subjectForUser(final String userName) {
        final SimpleUserPrincipal principal = new SimpleUserPrincipal(userName);

        final Set<Principal> principals = new HashSet<>();
        principals.add(principal);

        return new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
    }

    /**
     * Invokes the specified method on the target in the JAAS Context of the given subject
     * 
     * @param jaasSubject
     *            the subject in whose context the method should be invoked
     * @param target
     *            the target object of the method invocation or <code>null</code> if it is a static method
     * @param method
     *            the method to be invoked
     * @param params
     *            the parameters passed to the method
     * @return the result of the method invocation
     */
    public static <T> T invokeAs(
            final Subject jaasSubject,
            final Object target,
            final Method method,
            final Object... params) {

        return Subject.doAs(jaasSubject, new PrivilegedAction<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public T run() {
                try {
                    return (T) method.invoke(target, params);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (final InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        });

    }

    /**
     * Invokes the specified method on the target in the JAAS Context of the given user
     * 
     * @param user
     *            the user in whose context the method should be invoked
     * @param target
     *            the target object of the method invocation or <code>null</code> if it is a static method
     * @param method
     *            the method to be invoked
     * @param params
     *            the parameters passed to the method
     * @return the result of the method invocation
     */
    public static <T> T invokeAs(final String user, final Object target, final Method method, final Object... params) {
        return invokeAs(subjectForUser(user), target, method, params);
    }

}
