package li.moskito.test.jcr;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRSession extends ExternalResource {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JCRSession.class);

    private Session adminSession;
    private Session anonSession;
    private final Map<String, Session> userSessions = new HashMap<>();

    private final ContentRepository repositoryRule;
    private final String password;
    private final String username;

    public JCRSession(final ContentRepository repository) {
        this(repository, null, null);
    }

    public JCRSession(final ContentRepository repository, final String username, final String password) {
        this.repositoryRule = repository;
        this.username = username;
        this.password = password;
    }

    /**
     * Closes all sessions
     */
    @Override
    protected void after() {
        super.after();
        if (this.adminSession != null) {
            LOG.info("Logging off {}", this.adminSession.getUserID());
            this.adminSession.logout();
            this.adminSession = null;
        }
        if (this.anonSession != null) {
            LOG.info("Logging off {}", this.anonSession.getUserID());
            this.anonSession.logout();
            this.anonSession = null;
        }
        for (final Session session : this.userSessions.values()) {
            LOG.info("Logging off {}", session.getUserID());
            session.logout();
        }
        this.userSessions.clear();
        LOG.info("Closed all sessions");
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        initializeContentModel();
    }

    /**
     * Override this method to register customer node types
     */
    protected void initializeContentModel() throws Exception {

    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repositoryRule.getRepository();
    }

    /**
     * Logs into the repository. If a username and password has been specified, is is used for the login, otherwise an
     * anonymous login is done.
     * 
     * @return the session for the login
     * @throws RepositoryException
     * @throws LoginException
     */
    public Session login() throws RepositoryException {
        final Session session;
        if (username != null && password != null) {
            session = getRepository().login(new SimpleCredentials(username, password.toCharArray()));
            this.userSessions.put(username, session);
        } else if (anonSession == null) {
            anonSession = getRepository().login();
            session = anonSession;
        } else {
            session = anonSession;
        }
        return session;
    }

    /**
     * Creates a login for the given username and password
     * 
     * @param username
     *            the username to log in
     * @param password
     *            the password to log in
     * @return the session for the user
     * @throws RepositoryException
     */
    public Session login(final String username, final String password) throws RepositoryException {
        if (!this.userSessions.containsKey(username)) {
            this.userSessions.put(username,
                    getRepository().login(new SimpleCredentials(username, password.toCharArray())));
        }
        return this.userSessions.get(username);
    }

    /**
     * @return a session with priviledges access rights
     * @throws RepositoryException
     */
    public Session getAdminSession() throws RepositoryException {
        if (this.adminSession == null) {
            this.adminSession = getRepository().login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        return this.adminSession;
    }
}
