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

package io.parsingdata.metal.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Util.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static junit.framework.TestCase.assertFalse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class ParseValueTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Token definition;
    private ParseValue value;

    @Before
    public void setUp() {
        definition = def("value", 1);
        value = new ParseValue("value", definition, createFromBytes(new byte[] { 1 }), enc());
    }

    @Test
    public void state() {
        assertThat(value.name, is("value"));
        assertThat(value.getDefinition(), is(definition));
        assertThat(value.slice.offset, is(0L));
        assertThat(value.getValue(), is(equalTo(new byte[] { 1 })));
    }

    @Test
    public void matching() {
        assertTrue(value.matches("value"));

        assertFalse(value.matches("lue"));
        assertFalse(value.matches(".value"));
    }

    @Test
    public void toStringTest() {
        assertThat(value.toString(), is("pval(value:0x01)"));
    }

    @Test
    public void valueIsAValue() {
        assertTrue(value.isValue());
        assertThat(value.asValue(), is(sameInstance(value)));
    }

    @Test
    public void valueIsNotARef() {
        assertFalse(value.isReference());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert to ParseReference");
        value.asReference();
    }

    @Test
    public void valueIsNotAGraph() {
        assertFalse(value.isGraph());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert to ParseGraph");
        value.asGraph();
    }

    @Test
    public void emptyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name may not be empty.");

        new ParseValue("", definition, createFromBytes(new byte[] { 1 }), enc());
    }

}