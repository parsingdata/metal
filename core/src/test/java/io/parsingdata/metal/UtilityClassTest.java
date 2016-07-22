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

package io.parsingdata.metal;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilityClassTest {

    private void checkUtilityClass(Class<?> c) throws ReflectiveOperationException {
        assertTrue(Modifier.isFinal(c.getModifiers()));
        final Constructor<?>[] cons = c.getDeclaredConstructors();
        assertEquals(1, cons.length);
        assertTrue(Modifier.isPrivate(cons[0].getModifiers()));
        cons[0].setAccessible(true);
        cons[0].newInstance();
        for (Method m : c.getDeclaredMethods()) {
            assertTrue(Modifier.isStatic(m.getModifiers()));
        }
    }

    @Test
    public void shorthand() throws ReflectiveOperationException {
        checkUtilityClass(Shorthand.class);
    }

    @Test
    public void util() throws ReflectiveOperationException {
        checkUtilityClass(Util.class);
    }

}
