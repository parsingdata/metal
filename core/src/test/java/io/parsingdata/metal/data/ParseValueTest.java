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

import io.parsingdata.metal.token.Token;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ParseValueTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Token _definition;
    private ParseValue _value;

    @Before
    public void setUp() {
        _definition = def("value", 1);
        _value = new ParseValue("value", _definition, 0, new byte[] { 1 }, enc());
    }

    @Test
    public void state() {
        assertThat(_value.name, is("value"));
        assertThat(_value.getDefinition(), is(_definition));
        assertThat(_value.getOffset(), is(0L));
        assertThat(_value.getValue(), is(equalTo(new byte[] { 1 })));
    }

    @Test
    public void matching() {
        assertTrue(_value.matches("value"));

        assertFalse(_value.matches("lue"));
        assertFalse(_value.matches(".value"));
    }

    @Test
    public void toStringTest() {
        assertThat(_value.toString(), is("value(0x01)"));
    }

    @Test
    public void valueIsAValue() {
        assertTrue(_value.isValue());
        assertThat(_value.asValue(), is(sameInstance(_value)));
    }

    @Test
    public void valueIsNotARef() {
        assertFalse(_value.isRef());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseValue to ParseRef");
        _value.asRef();
    }

    @Test
    public void valueIsNotAGraph() {
        assertFalse(_value.isGraph());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseValue to ParseGraph");
        _value.asGraph();
    }

}