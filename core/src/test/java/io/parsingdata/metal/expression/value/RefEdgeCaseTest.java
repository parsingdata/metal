/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;

public class RefEdgeCaseTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private ParseState parseState;

    @Before
    public void before() {
        parseState = rep(any("a")).parse(env(stream(1, 2, 3))).get();
    }

    @Test
    public void emptyLimit() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Limit must evaluate to a non-empty value.");
        ref("a", last(ref("b"))).eval(parseState, enc());
    }

    @Test
    public void nanLimit() {
        final ImmutableList<Value> result = ref("a", last(div(con(1), con(0)))).eval(parseState, enc());
        assertEquals(1, result.size);
        assertEquals(NOT_A_VALUE, result.head);
    }

}
