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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;

/**
 * Inkstand launcher. It's not just an alternative way of starting the CDI Container. The launcher also provides
 * an extensible mechanism for processing command line arguments. The command line argument have to be of the format
 * <code>-[name] [value]/code>, with value being optional (for flag-arguments). The mechanism can be extended
 * dynamically by modules on the classpath that provide their own implementation of the {@link LauncherArgs} interface
 * and register them as service. The Inkstand launcher will search those extensions and passed the matching cmd line
 * arguments for processing to them.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public final class Inkstand {

    private Inkstand() {
    }

    public static void main(final String... args) {

        //get the name value pairs from the arguments
        final Map<String, String> argValues = argsToMap(args);
        applyArgs(argValues);

        CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot(argValues);
        cdiContainer.getContextControl().startContexts();
    }

    /**
     * Applies the name-value pairs of arguments to the registered extensions
     * @param argValues
     *  the argValues as key-value map. Values may be {@code null}.
     */
    private static void applyArgs(final Map<String, String> argValues) {

        final ServiceLoader<LauncherArgs> launcherArgsSPIs = ServiceLoader.load(LauncherArgs.class);
        final Set<String> unknownArgs = new HashSet<>(argValues.keySet());

        for(LauncherArgs launcherArgSPI : launcherArgsSPIs){
            unknownArgs.removeAll(processArgs(launcherArgSPI, argValues));
        }
        if(!unknownArgs.isEmpty()){
            throw new IllegalArgumentException("Unknown arguments " + unknownArgs);
        }
    }

    /**
     * Processes the arguments in the map by passing them to the given LauncherArgs instance. All arguments that
     * are recognized by this LauncherArgs are returned in the resulting set of known arguments.
     * @param launcherArgSPI
     *  the LauncherArgs instance to process the given arguments
     * @param argValues
     *  the arguments as key-value pairs to be passed to the LauncherArgs
     * @return
     *  a set of all argument names in the argValues map that are known to the launcher args. Callers may use this
     *  information to detect unsupported arguments.
     */
    private static Set<String> processArgs(final LauncherArgs launcherArgSPI,
                                           final Map<String, String> argValues) {

        final Set<String> knownArgs = new HashSet<>();

        for(String argName : launcherArgSPI.getArgNames()) {
            if(argValues.containsKey(argName)){
                launcherArgSPI.apply(argName, argValues.get(argName));
                knownArgs.add(argName);
            }
        }
        return knownArgs;

    }

    /**
     * Parses the cmd line arguments into a map. If the arguments starts with a '-' it denotes the argument name. If
     * the following argument does not start with a '-' it's the corresponding value. If not, the value is {@code null}.
     * @param args
     *  the cmd line arguments
     * @return
     *  the arguments as a name-value map.
     */
    private static Map<String, String> argsToMap(final String... args) {

        final Map<String, String> argValues = new HashMap<>();

        String argName = null;
        for(String arg : args ){
            if(arg.startsWith("-")) {
                if(argName != null) {
                    argValues.put(argName, null);
                }
                argName = arg.substring(1);
            } else if(argName != null){
                argValues.put(argName, arg);
                argName = null;
            } else {
                throw new IllegalArgumentException(arg + " is no valid argument");
            }
        }
        if(argName != null) {
            argValues.put(argName, null);
        }

        return argValues;
    }
}
