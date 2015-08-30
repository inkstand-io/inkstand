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

import java.util.Set;

/**
 * Configuration for securing a web resource.
 * Created by Gerald on 17.06.2015.
 */
public interface ResourceSecurityConfiguration {

    /**
     * The name of the security realm
     * @return
     */
    String getRealm();

    /**
     * The authentication method to use
     * @return
     */
    String getAuthenticationMethod();

    /**
     * Set of role names for the security realm
     * @return
     */
    Set<String> getSecurityRoles();

    /**
     * Sets of role names that are allowed to access the resources
     * @return
     */
    Set<String> getAllowedRoles();

    /**
     * A set of URL patterns denoting the protected resource.
     * @return
     *  set of string patterns
     */
    Set<String> getProtectedResources();

}
