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

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import org.slf4j.Logger;

/**
 * {@link LauncherArgs} extension that is activated with the {@code -?} flag switch and will display the descriptions
 * of all active arguments.
 * Created by Gerald on 31.07.2015.
 */
public class HelpLauncherArgs implements LauncherArgs {

    private static final Logger CONSOLE = getLogger("CONSOLE");

    @Override
    public Set<String> getArgNames() {

        return new HashSet<>(Arrays.asList("?"));
    }

    @Override
    public void apply(final String argName, final String value) {

        final ServiceLoader<LauncherArgs> loader = ServiceLoader.load(LauncherArgs.class);

        CONSOLE.info("Allowed arguments");
        for(LauncherArgs args : loader) {
            for(String arg : args.getArgNames()){
                CONSOLE.info("-{} \t {}", arg, args.getDescription(arg));
            }
        }
    }

    @Override
    public String getDescription(final String argName) {

        return "Displays a list of all available command line arguments.";
    }
}
