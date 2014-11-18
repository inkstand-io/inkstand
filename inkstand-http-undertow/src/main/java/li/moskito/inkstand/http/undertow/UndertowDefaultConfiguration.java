package li.moskito.inkstand.http.undertow;

import javax.inject.Inject;

import li.moskito.inkstand.config.WebServerConfiguration;

import org.apache.deltaspike.core.api.config.ConfigProperty;

public class UndertowDefaultConfiguration implements WebServerConfiguration {

    @Inject
    @ConfigProperty(name = "inkstand.http.port",
            defaultValue = "80")
    private int port;

    @Inject
    @ConfigProperty(name = "inkstand.http.listenaddress",
            defaultValue = "localhost")
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
