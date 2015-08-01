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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Gerald on 31.07.2015.
 */
public class TestLauncherArgs implements LauncherArgs {

    private static Map<String,String> appliedArgs = new HashMap<>();
    private static AtomicBoolean applyInvoked = new AtomicBoolean();

    @Override
    public Set<String> getArgNames() {

        return new HashSet<>(Arrays.asList("test", "other"));
    }

    @Override
    public void apply(final String argName, final String value) {
        this.applyInvoked.set(true);
        appliedArgs.put(argName, value);
    }

    @Override
    public String getDescription(final String argName) {

        return "testDescription";
    }

    public static Map<String, String> getAppliedArgs() {

        return appliedArgs;
    }

    public static boolean isApplyInvoked() {

        return applyInvoked.get();
    }

    public static void reset() {
        applyInvoked.set(false);
        appliedArgs.clear();
    }
}
