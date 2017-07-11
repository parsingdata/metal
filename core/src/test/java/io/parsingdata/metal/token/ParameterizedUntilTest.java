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

package io.parsingdata.metal.token;

import static java.nio.charset.StandardCharsets.US_ASCII;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.until;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.util.ParameterizedParse;

public class ParameterizedUntilTest extends ParameterizedParse {

    @Parameters(name = "{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[a,b,c,a,b,c] i=0,s=1,m=6 ab",    untilToken(0, 1, 6, con('c'), con("ab")),    stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=3,s=1,m=6 abcab", untilToken(3, 1, 6, con('c'), con("abcab")), stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=0,s=2,m=6 ab",    untilToken(0, 2, 6, con('c'), con("ab")),    stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=1,s=2,m=6 abcab", untilToken(1, 2, 6, con('c'), con("abcab")), stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=0,s=3,m=6",       untilToken(0, 3, 6, con('c'), con("ab")),    stream("abcabc", US_ASCII), enc(), false },
            //{ "[a,b,c,a,b,c] i=0,s=0,m=6",       untilToken(0, 0, 6, con('c'), con("ab")),    stream("abcabc", US_ASCII), enc(), false },
        });
    }

    private static Token untilToken(final int initial, final int step, final int max, final ValueExpression terminator, final ValueExpression expectedValue) {
        return post(until("value", con(initial), con(step), con(max), def("terminator", 1, eq(terminator))), eq(last(ref("value")), expectedValue));
    }

}
