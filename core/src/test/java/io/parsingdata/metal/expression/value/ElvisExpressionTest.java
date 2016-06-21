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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.elvis;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public class ElvisExpressionTest {

    private final Token choice = cho(
        def("a", 1, eq(con(1))),
        def("b", 1, eq(con(2)))
    );

    private final ValueExpression elvisExpression = elvis(ref("a"), ref("b"));

    @Test
    public void elvisLeft() throws IOException { // the building
        final ParseResult result = choice.parse(stream(1), enc());
        final OptionalValue eval = elvisExpression.eval(result.getEnvironment(), enc());

        assertTrue(eval.isPresent());
        assertThat(eval.get().asNumeric().intValue(), is(equalTo(1)));
    }

    @Test
    public void elvisRight() throws IOException {
        final ParseResult result = choice.parse(stream(2), enc());
        final OptionalValue eval = elvisExpression.eval(result.getEnvironment(), enc());

        assertTrue(eval.isPresent());
        assertThat(eval.get().asNumeric().intValue(), is(equalTo(2)));
    }

    @Test
    public void elvisNone() throws IOException {
        final ParseResult result = choice.parse(stream(3), enc());
        final OptionalValue eval = elvisExpression.eval(result.getEnvironment(), enc());

        assertFalse(eval.isPresent());
    }

    @Test
    public void toStringTest() {
        assertThat(elvisExpression.toString(), is(equalTo("Elvis(Ref(a),Ref(b))")));
    }
}
