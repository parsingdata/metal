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

import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ElvisExpressionTest {

    private final Token choice = cho(
        def("a", 1, eq(con(1))),
        def("b", 1, eq(con(2)))
    );

    private final ValueExpression elvisExpression = elvis(ref("a"), ref("b"));

    @Test
    public void elvisLeft() throws IOException { // the building
        final ParseResult result = choice.parse(stream(1), enc());
        final OptionalValueList eval = elvisExpression.eval(result.getEnvironment(), enc());

        assertNotNull(eval);
        assertEquals(1, eval.size);
        assertThat(eval.head.get().asNumeric().intValue(), is(equalTo(1)));
    }

    @Test
    public void elvisRight() throws IOException {
        final ParseResult result = choice.parse(stream(2), enc());
        final OptionalValueList eval = elvisExpression.eval(result.getEnvironment(), enc());

        assertNotNull(eval);
        assertEquals(1, eval.size);
        assertThat(eval.head.get().asNumeric().intValue(), is(equalTo(2)));
    }

    @Test
    public void elvisNone() throws IOException {
        final ParseResult result = choice.parse(stream(3), enc());
        final OptionalValueList eval = elvisExpression.eval(result.getEnvironment(), enc());

        assertNotNull(eval);
        assertEquals(1, eval.size);
    }

    @Test
    public void toStringTest() {
        assertThat(elvisExpression.toString(), is(equalTo("Elvis(Ref(a),Ref(b))")));
    }
}
