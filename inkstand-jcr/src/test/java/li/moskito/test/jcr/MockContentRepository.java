package li.moskito.test.jcr;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.jcr.Repository;

import org.mockito.Mockito;

/**
 * A ContentRepository implementation that creates a deep-stubbed mock using Mockito. Mock behavior can be defined by
 * using the {@code when} method of {@link Mockito} on the {@link Repository} that can be obtained by
 * {@code getRepository} <br>
 * Example:<br>
 * 
 * <pre>
 * <code>
 * final Repository mock = mockRepository.injectTo(subject).getRepository();
 * when(mock.login()).thenThrow(LoginException.class);
 * </code>
 * </pre>
 * 
 * The MockContentRepository can be initialized per test or per class.
 * 
 * @author gmuecke
 * 
 */
public class MockContentRepository extends ContentRepository {

    public MockContentRepository() {
        super(null);
    }

    /**
     * Creates a repository mock.
     */
    @Override
    protected Repository createRepository()
            throws IOException {
        return mock(Repository.class, RETURNS_DEEP_STUBS);
    }

    @Override
    protected void destroyRepository() {

    }

}
