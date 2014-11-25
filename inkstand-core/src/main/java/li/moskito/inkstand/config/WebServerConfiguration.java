package li.moskito.inkstand.config;

/**
 * Configuration Interface for the WebServer
 * 
 * @author Gerald Muecke, gerald@moskito.li
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
