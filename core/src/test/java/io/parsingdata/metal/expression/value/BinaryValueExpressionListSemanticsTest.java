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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.*;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

public class BinaryValueExpressionListSemanticsTest extends ParameterizedParse {

    @Parameterized.Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "a, a, b, b, last(a+b)", pred2(eq(last(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 6), enc(), true },
            { "a, a, b, b, first(a+b)", pred2(eq(first(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 4), enc(), true },
            { "a, a, a, b, b, last(a+b)", pred3(eq(last(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 5, 8), enc(), true },
            { "a, a, a, b, b, first(a+b)", pred3(eq(first(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 5, 5), enc(), false },
            { "a, a, b, b, last(offset(a)+offset(b))", pred2(eq(last(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 4), enc(), true },
            { "a, a, b, b, first(offset(a)+offset(b))", pred2(eq(first(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 2), enc(), true },
            { "a, a, a, b, b, last((offset(a)+offset(b))", pred3(eq(last(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 5, 6), enc(), true },
            { "a, a, a, b, b, first(offset(a)+offset(b))", pred3(eq(first(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 5, 3), enc(), false },
            { "a, a, a, b, b, last(a)+first(b)", pred3(eq(add(last(ref("a")), first(ref("b"))))), stream(1, 2, 3, 4, 5, 7), enc(), true },
            { "a, a, a, b, b, first(a)+last(b)", pred3(eq(add(first(ref("a")), last(ref("b"))))), stream(1, 2, 3, 4, 5, 6), enc(), true },
            { "a, a, b, b, last(not(a)+not(b))", pred2(eq(last(add(not(ref("a")), not(ref("b")))))), stream(1, 240, 3, 15, -1), enc(), true },
            { "a, a, b, b, first(not(a)+not(b))", pred2(eq(first(add(not(ref("a")), not(ref("b")))))), stream(230, 2, 25, 4, -1), enc(), true },
            { "a, a, a, b, b, last(not(a)+not(b))", pred3(eq(last(add(not(ref("a")), not(ref("b")))))), stream(1, 2, 200, 4, 55, -1), enc(), true },
            { "a, a, a, b, b, first(not(a)+not(b))", pred3(eq(first(add(not(ref("a")), not(ref("b")))))), stream(200, 2, 3, 55, 5, -1), enc(), false },
        });
    }

    public BinaryValueExpressionListSemanticsTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static Token pred2(Expression pred) {
        return
            seq(any("a"),
                any("a"),
                any("b"),
                any("b"),
                def("c", con(1), pred));
    }

    private static Token pred3(Expression pred) {
        return
            seq(any("a"),
                any("a"),
                any("a"),
                any("b"),
                any("b"),
                def("c", con(1), pred));
    }

}
