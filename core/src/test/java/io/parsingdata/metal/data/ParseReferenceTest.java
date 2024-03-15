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

import static java.math.BigInteger.ZERO;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.data.selection.ByTypeTest.EMPTY_SOURCE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.token.Token;

public class ParseReferenceTest {

    private Token definition;
    private ParseReference reference;

    @BeforeEach
    public void setUp() {
        definition = sub(def("value", 1), con(0));
        reference = new ParseReference(ZERO, EMPTY_SOURCE, definition);
    }

    @Test
    public void state() {
        assertThat(reference.location.longValueExact(), is(0L));
        assertThat(reference.getDefinition(), is(definition));
    }

    @Test
    public void toStringTest() {
        assertThat(reference.toString(), is("pref(@0)"));
    }

    @Test
    public void referenceIsAReference() {
        assertTrue(reference.isReference());
        assertThat(reference.asReference(), is(sameInstance(reference)));
    }

    @Test
    public void referenceIsNotAValue() {
        assertFalse(reference.isValue());

        final Exception e = assertThrows(UnsupportedOperationException.class, () -> reference.asValue());
        assertEquals("Cannot convert to ParseValue.", e.getMessage());
    }

    @Test
    public void referenceIsNotAGraph() {
        assertFalse(reference.isGraph());

        final Exception e = assertThrows(UnsupportedOperationException.class, () -> reference.asGraph());
        assertEquals("Cannot convert to ParseGraph.", e.getMessage());
    }

}