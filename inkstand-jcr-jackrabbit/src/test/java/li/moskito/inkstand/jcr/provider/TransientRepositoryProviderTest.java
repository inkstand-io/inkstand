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

import li.moskito.test.Scribble;

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
        Scribble.injectInto(this.subject).configProperty("inkstand.jcr.transient.configURL",
                getClass().getResource("/repository.xml").toString());
    }

    @Test
    public void testGetRepository() throws Exception {
        assertEquals(repository, subject.getRepository());
    }

    @Test
    public void testStartRepository_noCndFile() throws Exception {
        // check the repository does not perform a login as it is still a mock
        assertNull(this.subject.getRepository().login());
        // start the repository
        this.subject.startRepository();
        // the repository should be working
        Session session = subject.getRepository().login();
        assertNotNull(session);
        NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        assertFalse(ntm.hasNodeType("test:testType"));
    }

    @Test
    public void testStartRepository_withCndFile() throws Exception {
        // check the repository does not perform a login as it is still a mock
        assertNull(this.subject.getRepository().login());
        Scribble.injectInto(subject).configProperty("inkstand.jcr.transient.cndFileURL", "transient_jcr_model.cnd.txt");
        // start the repository
        this.subject.startRepository();
        // the repository should be working
        Session session = subject.getRepository().login();
        assertNotNull(session);
        NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        assertTrue(ntm.hasNodeType("test:testType"));
    }

    @Test
    public void testShutdownRepository_success() throws Exception {
        this.subject.shutdownRepository(repository);
        verify(repository).shutdown();
    }

    @Test(expected = RuntimeException.class)
    public void testShutdownRepository_exceptionOnShutdown() throws Exception {
        doThrow(IOException.class).when(repository).shutdown();
        this.subject.shutdownRepository(repository);
    }

}
