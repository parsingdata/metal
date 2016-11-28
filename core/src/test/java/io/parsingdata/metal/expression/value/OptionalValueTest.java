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

package io.parsingdata.metal.expression.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static junit.framework.TestCase.assertFalse;

import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ByteArraySlice;

public class OptionalValueTest {

    private static final Value VALUE = new Value(new ByteArraySlice(new byte[] { 1 }), enc());
    private static final OptionalValue OPTIONAL_VALUE = OptionalValue.of(VALUE);
    private static final OptionalValue EMPTY = OptionalValue.empty();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void withValue() {
        assertTrue(OPTIONAL_VALUE.isPresent());
        assertEquals("OptionalValue(0x01)", OPTIONAL_VALUE.toString());
        assertEquals(OPTIONAL_VALUE.get(), VALUE);
    }

    @Test
    public void withoutValue() {
        assertFalse(EMPTY.isPresent());
        assertEquals("OptionalValue(empty)", EMPTY.toString());

        thrown.expectMessage("OptionalValue instance is empty.");
        thrown.expect(NoSuchElementException.class);
        EMPTY.get();
    }

}