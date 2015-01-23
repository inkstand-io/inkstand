package li.moskito.inkstand.jcr.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.inkstand.scribble.Scribble;

import java.io.IOException;
import java.nio.file.Path;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeManager;

import li.moskito.inkstand.InkstandRuntimeException;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.SessionListener;
import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryRepositoryProviderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Path tempfolder;

    @Mock
    private TransientRepository repository;

    @InjectMocks
    private InMemoryRepositoryProvider subject;

    private ClassLoader contextClassLoader;

    @Before
    public void setUp() throws Exception {
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.configURL")
        .value(getClass().getResource("/repository.xml").toString());
        contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    @Test
    public void testGetRepository() throws Exception {
        assertEquals(repository, subject.getRepository());
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testStartRepository_brokenRepositoryXml_exceptionOnStartup() throws Exception {
        // prepare
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.configURL")
        .value(getClass().getResource("/broken_repository.xml").toString());
        // act
        subject.startRepository();
        // assert
        final Session session = subject.getRepository().login();
        assertNotNull(session);
    }

    @Test
    public void testStartRepository_defaultConfigurationXml() throws Exception {
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.configURL").defaultValue();
        Thread.currentThread().setContextClassLoader(null);
        // start the repository
        subject.startRepository();
    }

    @Test
    public void testStartRepository_externalConfigXml() throws Exception {
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.configURL")
        .value(getClass().getResource("InMemoryRepositoryProviderTest_externalConfig.xml").toString());
        Thread.currentThread().setContextClassLoader(null);
        // start the repository
        subject.startRepository();
    }

    @Test
    public void testStartRepository_noCndFile() throws Exception {
        // check the repository does not perform a login as it is still a mock
        assertNull(subject.getRepository().login());
        // start the repository
        subject.startRepository();
        // the repository should be working
        final Session session = subject.getRepository().login();
        assertNotNull(session);
        final NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        assertFalse(ntm.hasNodeType("test:testType"));
    }

    @Test
    public void testStartRepository_withCndFile() throws Exception {
        // check the repository does not perform a login as it is still a mock
        assertNull(subject.getRepository().login());
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.cndFileURL")
        .value("InMemoryRepositoryProviderTest_testStartRepository.cnd");
        // start the repository
        subject.startRepository();
        // the repository should be working
        final Session session = subject.getRepository().login();
        assertNotNull(session);
        final NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        assertTrue(ntm.hasNodeType("test:testType"));
    }

    @Test
    public void testShutdownRepository_providedRepositoryNoStartShutdown_success() throws Exception {
        // repository is not started
        subject.shutdownRepository(repository);
        verify(repository).shutdown();
    }

    @Test
    public void testShutdownRepository_providedRepositoryStartShutdown_success() throws Exception {
        // prepare
        // start the repository and obtain a session
        subject.startRepository();
        final Repository repo = subject.getRepository();
        final SessionImpl session = (SessionImpl) repo.login();
        // the shutdown can be verified if all sessions are logged out
        final SessionListener listener = mock(SessionListener.class);
        session.addListener(listener);
        // act
        subject.shutdownRepository(repo);
        // assert
        // shutdown can only be verified indirectly, when all session are logged out
        verify(listener).loggedOut(any(SessionImpl.class));
    }

    @Test
    public void testShutdownRepository_foreignRepository_success() throws Exception {
        final TransientRepository foreigRepository = mock(TransientRepository.class);
        subject.shutdownRepository(foreigRepository);
        verify(foreigRepository, times(0)).shutdown();
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testShutdownRepository_exceptionOnShutdown() throws Exception {
        doThrow(IOException.class).when(repository).shutdown();
        subject.shutdownRepository(repository);
    }

}
