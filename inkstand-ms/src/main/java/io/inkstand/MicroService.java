package io.inkstand;

/**
 * Interface to for a webserver. Inkstand is extensible to support various embeddable webservers.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
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
