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

package io.inkstand;

/**
 * Base class for checked exception that should require special handling. Recommended use is for business related
 * exception cases.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class InkstandException extends Exception {

    private static final long serialVersionUID = 7946317132691841679L;

    /**
     * Default constructor. The use is discouraged as it does not provide any meaningful information about the problem
     * that causes the exception.
     */
    public InkstandException() {
        super();
    }

    /**
     * Constructor that may be used to wrap an existing exception
     *
     * @param message
     *            message to indicate the higher-level cause of the exception
     * @param cause
     *            the actual cause of the exception
     */
    public InkstandException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor that may be used to create a root-cause exception.
     *
     * @param message
     *            the message describing the problem causing the exception
     */
    public InkstandException(final String message) {
        super(message);
    }

    /**
     * Constructor that may be used to wrap an existing exception without additional high-level information
     *
     * @param cause
     *            the root cause of the exception.
     */
    public InkstandException(final Throwable cause) {
        super(cause);
    }

}
