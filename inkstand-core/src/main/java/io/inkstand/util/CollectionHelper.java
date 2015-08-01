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

package io.inkstand.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for dealing with collections.
 * Created by Gerald on 01.08.2015.
 */
public final class CollectionHelper {

    private CollectionHelper(){}

    /**
     * Creates an unmodifiable set from the given items.
     * @param items
     *  items to be put into a set
     * @param <E>
     *     type of the items in the set
     * @return
     *  an unmodifiable set of items.
     */
    public static <E> Set<E> asUnmodifiableSet(E ... items){
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(items)));
    }
}
