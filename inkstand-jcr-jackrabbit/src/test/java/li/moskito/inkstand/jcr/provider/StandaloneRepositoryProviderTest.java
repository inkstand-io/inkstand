package li.moskito.inkstand.jcr.provider;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jcr.Repository;

import li.moskito.scribble.Scribble;
import li.moskito.scribble.rules.ExternalFile;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

public class StandaloneRepositoryProviderTest {

    public TemporaryFolder folder = new TemporaryFolder();
    public ExternalFile file = new ExternalFile(folder, "repository.xml").withContent().fromResource(
            getClass().getResource("/repository.xml"));

    @Rule
    public RuleChain chain = RuleChain.outerRule(folder).around(file);

    private StandaloneRepositoryProvider subject;
    private Repository repository;

    @Before
    public void setUp() throws Exception {
        subject = new StandaloneRepositoryProvider();
        Scribble.injectInto(subject).configProperty("inkstand.jcr.home").value(folder.getRoot().getAbsolutePath());
    }

    @After
    public void tearDown() throws Exception {
        if (repository != null && repository instanceof RepositoryImpl) {
            ((RepositoryImpl) repository).shutdown();
        }
    }

    @Test
    public void testGetRepository_and_Close() throws Exception {
        repository = subject.getRepository();
        assertNotNull(repository);
    }

    @Test
    public void testClose_RepositoryImpl_shutdown() throws Exception {
        final RepositoryImpl repo = mock(RepositoryImpl.class);
        subject.close(repo);
        verify(repo).shutdown();
    }

    @Test
    public void testClose_NoRepositoryImpl_noShutdown() throws Exception {
        final org.apache.jackrabbit.core.TransientRepository repo = mock(org.apache.jackrabbit.core.TransientRepository.class);
        subject.close(repo);
        verify(repo, times(0)).shutdown();
    }

}
