package io.inkstand.config;

/**
 * Configuration Interface for the WebServer
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 * 
 */
public interface WebServerConfiguration {

    /**
     * The port to listen on
     * 
     * @return
     */
    public int getPort();

    /**
     * The bind address this server.
     * 
     * @return
     */
    public String getBindAddress();
}
