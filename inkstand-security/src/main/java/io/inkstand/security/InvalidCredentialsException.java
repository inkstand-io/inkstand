/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.security;

/**
 * Exception to throw when invalid credentials have been provided but the user itself was correct.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class InvalidCredentialsException extends AuthenticationException {

    private static final long serialVersionUID = -5630701679325764004L;

    public InvalidCredentialsException(final String userId) {
        super("Invalid credentials", userId);
    }

    public InvalidCredentialsException(final String userId, final Throwable cause) {
        super("Invalid credentials", userId, cause);
    }

}
