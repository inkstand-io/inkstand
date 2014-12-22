package li.moskito.inkstand.http.undertow;

import io.undertow.Undertow;

import javax.inject.Inject;

import li.moskito.inkstand.config.WebServerConfiguration;

import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * Default configuration for an {@link Undertow} web container that defines the listen address and the port. If none is
 * specified (via Delta Spike {@link ConfigProperty} injection), the default values localhost:80 will be used. The
 * property names are
 * <ul>
 * <li><code>inkstand.http.port</code></li>
 * <li><code>inkstand.http.listenaddress</code></li>
 * </ul>
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public class UndertowDefaultConfiguration implements WebServerConfiguration {

    @Inject
    @ConfigProperty(name = "inkstand.http.port", defaultValue = "80")
    private int port;

    @Inject
    @ConfigProperty(name = "inkstand.http.listenaddress", defaultValue = "localhost")
    private String bindAddress;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getBindAddress() {
        return bindAddress;
    }

}
