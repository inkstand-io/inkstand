package li.moskito.inkstand.http;

/**
 * Interface to for a webserver. Inkstand is extensible to support various embeddable webservers.
 * 
 * @author gmuecke
 * 
 */
public interface WebServer {

    /**
     * Starts the webserver
     */
    void start();

    /**
     * Stops the webserver
     */
    void stop();
}
