package io.inkstand.http.undertow;

import io.inkstand.MicroService;
import io.undertow.Undertow;

import javax.inject.Inject;

/**
 * Undertow based WebServer implementation
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class UndertowWebServer implements MicroService {

    @Inject
    private Undertow undertow;

    @Override
    public void start() {
        undertow.start();
    }

    @Override
    public void stop() {
        undertow.stop();
    }

    /**
     * @return the {@link Undertow} server instance.
     */
    public Undertow getUndertow() {
        return undertow;
    }

    @Override
    public String toString() {
        return "[Undertow]";
    }

}
