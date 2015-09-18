package io.inkstand.mgmt;

import static org.mockito.Mockito.mock;

import javax.enterprise.inject.spi.BeanManager;
import java.util.Map;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.ContextControl;

/**
 * Created by Gerald Mücke on 18.09.2015.
 */
public class MockCdiContainer implements CdiContainer {

    private boolean shutdown;
    private boolean boot;
    private BeanManager bm = mock(BeanManager.class);
    private ContextControl cc = mock(ContextControl.class);

    public boolean isBoot() {

        return boot;
    }

    public boolean isShutdown() {

        return shutdown;
    }

    @Override
    public void boot() {
        boot = true;
    }

    @Override
    public void boot(final Map<?, ?> properties) {
        boot();
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public BeanManager getBeanManager() {

        return bm;
    }

    @Override
    public ContextControl getContextControl() {

        return cc;
    }
}
