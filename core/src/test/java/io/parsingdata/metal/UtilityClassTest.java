/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.util.ClassDefinition.checkUtilityClass;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.Selection;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.data.selection.ByToken;
import io.parsingdata.metal.data.selection.ByType;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.ConstantFactory;

public class UtilityClassTest {

    // Check that utility classes are well-formed.
    @Test
    public void utilityClasses() throws ReflectiveOperationException {
        checkUtilityClass(Shorthand.class);
        checkUtilityClass(Util.class);
        checkUtilityClass(ByName.class);
        checkUtilityClass(ByToken.class);
        checkUtilityClass(ByType.class);
        checkUtilityClass(ConstantFactory.class);
        checkUtilityClass(Selection.class);
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
