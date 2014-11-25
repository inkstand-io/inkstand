package li.moskito.test.jcr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jcr.Repository;

import li.moskito.test.TestUtil;
import li.moskito.test.rules.ExternalResource;

import org.junit.rules.TemporaryFolder;

/**
 * Rule for testing with java content repositories
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
public abstract class ContentRepository extends ExternalResource {

    /**
     * The JCR Repository
     */
    private Repository repository;

    /**
     * The test run id.
     */
    private String testRunId;

    private final TemporaryFolder workingDirectory;

    private String jndiName;

    private boolean initialized;

    private boolean beforeExecuted;

    /**
     * Creates the content repository in the working directory
     * 
     * @param workingDirectory
     */
    public ContentRepository(final TemporaryFolder workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Initializes the repository
     */
    @Override
    protected void beforeClass()
            throws Throwable {
        super.before();
        this.testRunId = TestUtil.generateRunId();
        this.repository = createRepository();
        this.initialized = true;
    }

    /**
     * Destroys the repository
     */
    @Override
    protected void afterClass() {
        this.initialized = false;
        super.after();
        destroyRepository();

    }

    @Override
    protected void before()
            throws Throwable {
        if (!initialized) {
            beforeClass();
            this.beforeExecuted = true;
        }
    }

    @Override
    protected void after() {
        if (beforeExecuted) {
            afterClass();
        }

    }

    /**
     * The id of the test run. A TestRunId is created for every Suite and remains the same for all tests in that suite.
     * It is intended to be used when testing against a persistent repository to reduce the risk of node name
     * collisions.
     * 
     * @return the testRunId
     */
    public String getTestRunId() {
        return testRunId;
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @return the workingDirectory in which the repository and its configuration is located
     */
    protected TemporaryFolder getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Creates a transient repository with files in the local temp directory.
     * 
     * @return the created repository
     * 
     * @throws IOException
     * @throws ConfigurationException
     */
    protected abstract Repository createRepository()
            throws IOException;

    /**
     * Closes the admin session, and in case of local transient respository for unit test, shuts down the repository and
     * cleans all temporary files.
     * 
     * @throws IOException
     */
    protected abstract void destroyRepository();

    /**
     * Method to define the jndi lookup name for the repository. Use this mehtod in combination with the
     * {@code injectRepository} method to inject the test repository into a field of a test subject
     * 
     * @param jndiName
     *            the name for retrieving the repository. The name should match the lookup name configured for the
     *            injection target field of the test subject.
     * @return the ContentRepository rule itself supporting congiguration chaining.
     */
    public ContentRepository lookupName(final String jndiName) {
        this.jndiName = jndiName;
        return this;
    }

    /**
     * Injects the repository reference from the rule into a field annotated with {@link Resource} and a mapped name
     * that equals the configured {@code lookupName}
     * 
     * @param subject
     *            the subject into which the repository should be injected
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public ContentRepository injectTo(final Object subject)
            throws IllegalArgumentException, IllegalAccessException {
        assertNotNull("A jndiName must be defined", this.jndiName);
        Class<?> type = subject.getClass();
        while (!type.equals(Object.class)) {
            if (findAndSetRepository(subject, type)) {
                return this;
            }
            type = type.getSuperclass();
        }

        fail("No field with @Inject or @Resource(mappendName=\"" + jndiName + "\" found on " + subject.getClass());
        return null;
    }

    /**
     * Searchs the current type for a declared field that denotes a repository injection point. If such a field is
     * found, the {@link Repository} is injected and <code>true</code> is returned.
     * 
     * @param subject
     *            the subject into which the repository should be injected
     * @param type
     *            the current type to be searched for injectable declared fields
     * @return <code>true</code> when the injection was successful, false if not.
     * @throws IllegalAccessException
     */
    private boolean findAndSetRepository(final Object subject, final Class<?> type)
            throws IllegalAccessException {
        boolean result = false;
        // loop through all fields and inject where possible
        for (final Field f : type.getDeclaredFields()) {
            if (Repository.class.isAssignableFrom(f.getType()) && checkAnnotationAndInject(subject, f)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Checks the given field for a matching annotation. If an annotation that matches this repository is found, it is
     * injected
     * 
     * @param subject
     *            the subject into which the repository might be injected
     * @param f
     *            the field that is the potential injection point
     * @return <code>true</code> if the repository was injected
     * @throws IllegalAccessException
     */
    private boolean checkAnnotationAndInject(final Object subject, final Field f)
            throws IllegalAccessException {
        boolean result = false;
        final javax.annotation.Resource resAn = f.getAnnotation(javax.annotation.Resource.class);
        if (resAn != null && this.jndiName.equals(resAn.mappedName())) {
            f.setAccessible(true);
            f.set(subject, repository);
            result = true;
        }
        final Inject injectAn = f.getAnnotation(Inject.class);
        if (injectAn != null) {
            f.setAccessible(true);
            f.set(subject, repository);
            result = true;
        }
        return result;
    }

}
