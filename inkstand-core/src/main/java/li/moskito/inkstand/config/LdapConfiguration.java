package li.moskito.inkstand.config;

/**
 * Configuration for an LDAP Server.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public interface LdapConfiguration {

    /**
     * The credential used to authenticate the bind user
     * 
     * @return
     */
    public abstract String getBindCredentials();

    /**
     * The DN used for binding to the server.
     * 
     * @return a distinguished name
     */
    public abstract String getBindDn();

    /**
     * The hostname of the ldap server
     * 
     * @return the hostname
     */
    public abstract String getHostname();

    /**
     * The port number of the ldap server. IANA Default port number is 389.
     * 
     * @return the ldap server port
     */
    public abstract int getPort();

}
