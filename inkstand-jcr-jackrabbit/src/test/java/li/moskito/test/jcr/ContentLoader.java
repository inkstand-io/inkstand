package li.moskito.test.jcr;

import java.net.URL;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import li.moskito.inkstand.jcr.RepositoryProvider;
import li.moskito.inkstand.jcr.util.JCRUtil;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentLoader implements TestRule {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ContentLoader.class);

    private final RepositoryProvider provider;
    private final URL contentDescriptorUrl;

    public ContentLoader(final RepositoryProvider provider, final URL contentDescriptorUrl) {
        this.provider = provider;
        this.contentDescriptorUrl = contentDescriptorUrl;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {

        return new Statement() {

            @Override
            public void evaluate()
                    throws Throwable {
                try {
                    LOG.info("Loading Content");
                    final Repository repo = provider.getRepository();
                    final Session session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
                    JCRUtil.loadContent(session, contentDescriptorUrl);
                    session.logout();
                    base.evaluate();

                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }

            }

        };

    }

}
