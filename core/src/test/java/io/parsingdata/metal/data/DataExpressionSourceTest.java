/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class DataExpressionSourceTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    public ParseValue setupValue() {
        final Optional<ParseState> result = setupResult();
        assertTrue(result.isPresent());
        return getValue(result.get().order, "b");
    }

    private Optional<ParseState> setupResult() {
        final Token token =
            seq(def("a", con(4)),
                tie(def("b", con(2)), ref("a")));
        return token.parse(env(stream(1, 2, 3, 4)));
    }

    @Test
    public void createSliceFromParseValue() {
        final ParseValue value = setupValue();
        assertTrue(value.slice().source.isAvailable(ZERO, BigInteger.valueOf(4)));
        assertFalse(value.slice().source.isAvailable(ZERO, BigInteger.valueOf(5)));
    }

    @Test
    public void indexOutOfBounds() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("ValueExpression dataExpression yields 1 result(s) (expected at least 2).");
        final Optional<ParseState> result = setupResult();
        final DataExpressionSource source = new DataExpressionSource(ref("a"), 1, result.get(), enc());
        source.getData(ZERO, BigInteger.valueOf(4));
    }

    @Test
    public void notAValue() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("ValueExpression dataExpression yields NOT_A_VALUE at index 0.");
        new DataExpressionSource(div(con(1), con(0)), 0, EMPTY_PARSE_STATE, enc()).isAvailable(ZERO, ZERO);
    }

    @Test
    public void size() {
        final Optional<ParseState> result = setupResult();
        final DataExpressionSource source = new DataExpressionSource(ref("a"), 0, result.get(), enc());
        assertEquals(4, source.size().intValue());
    }

}
