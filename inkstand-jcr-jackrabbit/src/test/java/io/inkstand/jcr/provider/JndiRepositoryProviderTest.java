package io.inkstand.jcr.provider;

import static org.junit.Assert.assertEquals;

import javax.jcr.Repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JndiRepositoryProviderTest {

    @Mock
    private Repository repository;

    @InjectMocks
    private JndiRepositoryProvider subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRepository() throws Exception {
        assertEquals(repository, subject.getRepository());
    }

}
