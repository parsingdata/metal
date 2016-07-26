/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

package io.parsingdata.metal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassDefinition {

    public static void checkUtilityClass(Class<?> c) throws ReflectiveOperationException {
        final String simpleName = c.getSimpleName();
        // class is final
        assertTrue(simpleName + " should be final", Modifier.isFinal(c.getModifiers()));

        // has one constructor
        final Constructor<?>[] cons = c.getDeclaredConstructors();
        assertEquals(simpleName + " should have exactly 1 constructor", 1, cons.length);

        // which is private
        assertTrue(simpleName + " should have a private constructor", Modifier.isPrivate(cons[0].getModifiers()));

        // call it for coverage
        cons[0].setAccessible(true);
        cons[0].newInstance();

        // check that all declared methods are static
        for (final Method m : c.getDeclaredMethods()) {
            assertTrue("method '" + m.getName()  + "' in " + simpleName + " should be static", Modifier.isStatic(m.getModifiers()));
        }
    }

}
