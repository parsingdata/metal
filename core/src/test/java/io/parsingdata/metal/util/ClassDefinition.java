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

    public static void checkUtilityClass(Class<?> utilityClass) throws ReflectiveOperationException {
        final String simpleName = utilityClass.getSimpleName();
        // class is final
        assertTrue(simpleName + " should be final", Modifier.isFinal(utilityClass.getModifiers()));

        // has one constructor
        final Constructor<?>[] constructors = utilityClass.getDeclaredConstructors();
        assertEquals(simpleName + " should have exactly 1 constructor", 1, constructors.length);

        // which is private
        assertTrue(simpleName + " should have a private constructor", Modifier.isPrivate(constructors[0].getModifiers()));

        // call it for coverage
        constructors[0].setAccessible(true);
        constructors[0].newInstance();

        // check that all declared methods are static
        for (final Method method : utilityClass.getDeclaredMethods()) {
            assertTrue("method '" + method.getName()  + "' in " + simpleName + " should be static", Modifier.isStatic(method.getModifiers()));
        }
    }

}
