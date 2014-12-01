package li.moskito.inkstand.jcr;

/**
 * Interface that may be implemented by a dynamic proxy instance to obtain access to the proxied object.
 * @author Gerald Muecke, gerald@moskito.li
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
