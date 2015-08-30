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

package io.inkstand;

import java.util.Set;

/**
 * This Service Provider interface may be implemented by modules to provide information to the {@link Inkstand}
 * launcher which arg-line parameters are acceptable, how to validate them and how to map those to System properties.
 * <br/>
 * Implementations have to be specified using Java's {@link java.util.ServiceLoader} mechanism. That is, you
 * have to provide a file named like the fully qualified classname of this class ( {@code io.inkstand.LauncherArgs}
 * and put it in {@code META_INF/services}. The contents of this file have to be the fully qualified classnames
 * of the implemtentations.
 *
 * Created by Gerald on 31.07.2015.
 */
public interface LauncherArgs {

    /**
     * Provides the names of the command line arguments that are supported by the implementing module. Each command
     * line argument has to be specified with a leading dash, i.e. {@code -p} where 'p' is the name returned by this
     * method.
     * @return
     *  the names of the supported command line argument
     */
    Set<String> getArgNames();

    /**
     * Applies the value for the given argument. It's up to the implementation how to deal with the value, a typical
     * scenario is to set a system property for that module.
     * @param argName
     *  the argument name for which the value was specified
     * @param value
     *  the value to be validated
     * @throws IllegalArgumentException if the value is not valid
     *
     */
    void apply(String argName, String value);

    /**
     * Returns a description for the specified argument name. The description is used for printing out proper help
     * texts for the given argument.
     * @param argName
     *  the name for wich to return a description
     * @return
     *  the description for the specified argument
     */
    String getDescription(String argName);
}
