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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;

/**
 * Created by Gerald on 01.08.2015.
 */
public class CollectionHelperTest {

    @Test
    public void testAsUnmodifiableSet() throws Exception {
        //prepare

        //act
        Set<String> items = CollectionHelper.asUnmodifiableSet("one", "two", "three", "three");

        //assert

        assertNotNull(items);
        assertEquals(3, items.size());
        assertTrue(items.contains("one"));
        assertTrue(items.contains("two"));
        assertTrue(items.contains("three"));
    }
}