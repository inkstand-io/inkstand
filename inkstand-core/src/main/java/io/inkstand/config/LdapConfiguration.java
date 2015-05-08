/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.config;

/**
 * Configuration for an LDAP Server.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public interface LdapConfiguration {

    /**
     * The credential used to authenticate the bind user
     * 
     * @return
     *  the secret credential for the bind user
     */
    String getBindCredentials();

    /**
     * The DN used for binding to the server.
     * 
     * @return a distinguished name
     */
    String getBindDn();

    /**
     * The hostname of the ldap server
     * 
     * @return the hostname
     */
    String getHostname();

    /**
     * The port number of the ldap server. IANA Default port number is 389.
     * 
     * @return the ldap server port
     */
    int getPort();

}
