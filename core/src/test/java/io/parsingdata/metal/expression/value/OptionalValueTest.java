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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.expression.value.ConstantFactory.makeConstantSlice;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OptionalValueTest {

    private static final Value VALUE = new Value(makeConstantSlice(new byte[] { 1 }), enc());
    private static final Optional<Value> OPTIONAL_VALUE = Optional.of(VALUE);
    private static final Optional<Value> EMPTY = Optional.empty();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void withValue() {
        assertTrue(OPTIONAL_VALUE.isPresent());
        assertEquals("Optional[0x01]", OPTIONAL_VALUE.toString());
        assertEquals(OPTIONAL_VALUE.get(), VALUE);
    }

    @Test
    public void withoutValue() {
        assertFalse(EMPTY.isPresent());
        assertEquals("Optional.empty", EMPTY.toString());

        thrown.expectMessage("No value present");
        thrown.expect(NoSuchElementException.class);
        EMPTY.get();
    }

}