package io.inkstand.jcr;

/**
 * Interface that may be implemented by a dynamic proxy instance to obtain access to the proxied object.
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 *
 * @param <T>
 */
public interface DynamicProxy<T> {

    /**
     * @return
     *  the object proxied by the proxy
     */
    public T getProxiedObject();
}
