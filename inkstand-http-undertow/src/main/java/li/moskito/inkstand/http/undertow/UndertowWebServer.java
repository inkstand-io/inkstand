package li.moskito.inkstand.http.undertow;

import io.undertow.Undertow;

import javax.inject.Inject;

import li.moskito.inkstand.MicroService;

/**
 * Undertow based WebServer implementation
 *
 * @author Gerald Muecke, gerald@moskito.li
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

}
