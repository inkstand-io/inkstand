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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

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

        org.jboss.weld.environment.se.StartMain.main(args);
    }

    /**
     * Applies the name-value pairs of arguments to the registered extensions
     * @param argValues
     *  the argValues as key-value map. Values may be {@code null}.
     */
    private static void applyArgs(final Map<String, String> argValues) {

        final ServiceLoader<LauncherArgs> launcherArgsSPIs = ServiceLoader.load(LauncherArgs.class);
        for(LauncherArgs launcherArgSPI : launcherArgsSPIs){
            //TODO refactor two nested for-loops
            for(String argName : launcherArgSPI.getArgNames()) {
                if(argValues.containsKey(argName)){
                    launcherArgSPI.apply(argName, argValues.get(argName));
                }
            }
        }
        //TODO throw exception if there are arguments that could not be applied
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
        for(int i = 0; i < args.length; i++){

            //TODO refactor two nested ifs
            //the next check is pointless as all "invalid" arguments are ignored when being applied to the registered
            //LauncherArgs implementation. As long as that is the case, this line of code is prone to mutation
            if(args[i].startsWith("-")){
                String argName = args[i].substring(1);
                String argValue = null;
                if(i+1 < args.length && !args[i+1].startsWith("-")){
                    argValue = args[i+1];
                    i++;
                }
                argValues.put(argName, argValue);
            }
        }
        return argValues;
    }
}
