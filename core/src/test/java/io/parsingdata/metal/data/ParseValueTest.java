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

package io.parsingdata.metal.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.token.Token;

public class ParseValueTest {

    private Token definition;
    private ParseValue value;
    private ParseValue largerValue;

    @BeforeEach
    public void setUp() {
        definition = def("value", 1);
        value = new ParseValue("value", definition, createFromBytes(new byte[] { 1 }), enc());
        largerValue = new ParseValue("largerValue", definition, createFromBytes(new byte[] { 0, 1, 2, 3, 4 }), enc());
    }

    @Test
    public void state() {
        assertThat(value.name, is("value"));
        assertThat(value.getDefinition(), is(definition));
        assertThat(value.slice().offset.longValueExact(), is(0L));
        assertThat(value.value(), is(equalTo(new byte[] { 1 })));
    }

    @Test
    public void matching() {
        assertTrue(value.matches("value"));
        assertTrue(value.matches(definition));
        assertTrue(value.matches(def("value", 1)));

        assertFalse(value.matches(def("value", 2)));
        assertFalse(value.matches("lue"));
        assertFalse(value.matches(".value"));
    }

    @Test
    public void valueToStringTest() {
        assertThat(value.toString(), is("pval(value:0x01)"));
    }

    @Test
    public void largerValueToStringTest() {
        assertThat(largerValue.toString(), is("pval(largerValue:0x00010203...)"));
    }

    @Test
    public void valueIsAValue() {
        assertTrue(value.isValue());
        assertThat(value.asValue(), is(sameInstance(value)));
    }

    @Test
    public void valueIsNotARef() {
        assertFalse(value.isReference());
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, value::asReference);
        assertEquals("Cannot convert to ParseReference.", e.getMessage());
    }

    @Test
    public void valueIsNotAGraph() {
        assertFalse(value.isGraph());
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, value::asGraph);
        assertEquals("Cannot convert to ParseGraph.", e.getMessage());
    }

    @Test
    public void emptyName() {
        final Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> new ParseValue("", definition, createFromBytes(new byte[] { 1 }), enc()));
        assertEquals("Argument name may not be empty.", e.getMessage());
    }

}
