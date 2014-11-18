package li.moskito.inkstand.http.undertow;

import io.undertow.Undertow;

import javax.inject.Inject;

import li.moskito.inkstand.http.WebServer;

/**
 * Undertow based WebServer implementation
 * 
 * @author gmuecke
 * 
 */
public class UndertowWebServer implements WebServer {

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

    public Undertow getUndertow() {
        return undertow;
    }

}
