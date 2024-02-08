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

package io.parsingdata.metal.expression.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotAValueTest {

    @Test
    public void getSlice() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::slice);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

    @Test
    public void getEncoding() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::encoding);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

    @Test
    public void getValue() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::value);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

    @Test
    public void getLength() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::length);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

    @Test
    public void asNumeric() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::asNumeric);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

    @Test
    public void asString() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::asString);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

    @Test
    public void asBitSet() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, NOT_A_VALUE::asBitSet);
        assertEquals("NOT_A_VALUE does not support any Value operation.", e.getMessage());
    }

}
