package li.moskito.inkstand.jcr.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Path;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeManager;

import li.moskito.inkstand.InkstandRuntimeException;
import li.moskito.scribble.Scribble;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransientRepositoryProviderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Path tempfolder;

    @Mock
    private TransientRepository repository;

    @InjectMocks
    private TransientRepositoryProvider subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.configURL",
                getClass().getResource("/repository.xml").toString());
    }

    @Test
    public void testGetRepository() throws Exception {
        assertEquals(repository, subject.getRepository());
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testStartRepository_brokenRepositoryXml_exceptionOnStartup() throws Exception {
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.configURL",
                getClass().getResource("/broken_repository.xml").toString());
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
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.cndFileURL",
                "TransientRepositoryProviderTest_testStartRepository.cnd");
        // start the repository
        subject.startRepository();
        // the repository should be working
        final Session session = subject.getRepository().login();
        assertNotNull(session);
        final NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        assertTrue(ntm.hasNodeType("test:testType"));
    }

    @Test
    public void testShutdownRepository_success() throws Exception {
        subject.shutdownRepository(repository);
        verify(repository).shutdown();
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testShutdownRepository_exceptionOnShutdown() throws Exception {
        doThrow(IOException.class).when(repository).shutdown();
        subject.shutdownRepository(repository);
    }

}
