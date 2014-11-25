package li.moskito.inkstand;

/**
 * Interface to for a webserver. Inkstand is extensible to support various embeddable webservers.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
public interface MicroService {

    /**
     * Starts the webserver
     */
    void start();

    /**
     * Stops the webserver
     */
    void stop();
}
