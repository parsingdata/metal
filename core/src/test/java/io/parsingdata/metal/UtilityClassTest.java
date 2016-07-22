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

import io.parsingdata.metal.data.selection.*;
import io.parsingdata.metal.data.transformation.Reversal;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.token.Token;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static io.parsingdata.metal.Util.tokensToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilityClassTest {

    // Check that a class is final, has a single private constructor and that all
    // its declared methods are static.
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
    public void utilityClasses() throws ReflectiveOperationException {
        checkUtilityClass(Shorthand.class);
        checkUtilityClass(Util.class);
        checkUtilityClass(Reversal.class);
        checkUtilityClass(ByItem.class);
        checkUtilityClass(ByName.class);
        checkUtilityClass(ByOffset.class);
        checkUtilityClass(ByToken.class);
        checkUtilityClass(ByType.class);
    }

    // The method is used to print tokens for composing Tokens' toString()
    // implementation. Since they all require a non-zero amount of tokens, this
    // method must be explicitly tested.
    @Test
    public void zeroTokensToString() {
        assertEquals("", tokensToString(new Token[] {}));
    }

    // Metal uses enums to prevent the use of difficult to understand boolean arguments.
    // However, enums come with some inherited methods that are not of use internally.
    @Test
    public void inheritedEnumMethods() {
        assertEquals(2, Sign.values().length);
        assertEquals(Sign.SIGNED, Sign.valueOf("SIGNED"));
        assertEquals(Sign.UNSIGNED, Sign.valueOf("UNSIGNED"));
        assertEquals(2, ByteOrder.values().length);
        assertEquals(ByteOrder.BIG_ENDIAN, ByteOrder.valueOf("BIG_ENDIAN"));
        assertEquals(ByteOrder.LITTLE_ENDIAN, ByteOrder.valueOf("LITTLE_ENDIAN"));
    }

}
