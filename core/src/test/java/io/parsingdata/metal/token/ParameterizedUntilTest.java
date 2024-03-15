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

package io.parsingdata.metal.token;

import static java.nio.charset.StandardCharsets.US_ASCII;

import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.until;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.util.ParameterizedParse;

public class ParameterizedUntilTest extends ParameterizedParse {

    private static final Token ABC = def("abc", 3, eq(con("abc")));

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[a,b,c,a,b,c] i=0,s=1,m=6 ab",      untilToken(0, 1, 6, con('c'), con("ab"), ABC), stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=3,s=1,m=6 abcab",   untilToken(3, 1, 6, con('c'), con("abcab")),   stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=0,s=2,m=6 ab",      untilToken(0, 2, 6, con('c'), con("ab"), ABC), stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=1,s=2,m=6 abcab",   untilToken(1, 2, 6, con('c'), con("abcab")),   stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=0,s=3,m=6",         untilToken(0, 3, 6, con('c'), con("")),        stream("abcabc", US_ASCII), enc(), false },
            { "[a,b,c,a,b,c] i=0,s=0,m=6",         untilToken(0, 0, 6, con(""), con("")),         stream("abcabc", US_ASCII), enc(), false },
            { "[a,b,c,a,b,c] i=6,s=-1,m=0 abcab",  untilToken(6, -1, 0, con('c'), con("abcab")),  stream("abcabc", US_ASCII), enc(), true },
            { "[] i=0,s=-1,m=6",                   untilToken(0, -1, 6, con(""), con("")),        stream("", US_ASCII), enc(), false },
            { "[] i=6,s=1,m=0",                    untilToken(6, 1, 0, con(""), con("")),         stream("", US_ASCII), enc(), false },
            { "[a,b,c,a,b,c] i=(0,0),s=3,m=6",     untilToken(exp(con(0), con(2)), con(3), con(6), con('c'), con("")), stream("abcabc", US_ASCII), enc(), false },
            { "[a,b,c,a,b,c] i=(0,0),s=(3,3),m=6", untilToken(exp(con(0), con(2)), exp(con(3), con(2)), con(6), con('c'), con("")), stream("abcabc", US_ASCII), enc(), false },
            { "[] i=NaN",                          untilToken(div(con(1), con(0)), con(1), con(1), con(""), con("")), stream("", US_ASCII), enc(), false },
            { "[a,b,c,a,b,c] i=0,s=1 ab",          untilToken(0, 1, con('c'), con("ab"), ABC),    stream("abcabc", US_ASCII), enc(), true },
            { "[a,b,c,a,b,c] i=0 ab",              untilToken(0, con('c'), con("ab"), ABC),       stream("abcabc", US_ASCII), enc(), true },
            { "[a] i=2,s=1,m=6",                   untilTokenAlwaysTrueTerminator(2, 1, 6),       stream("a", US_ASCII), enc(), false },
        });
    }

    private static Token untilToken(final int initial, final int step, final int max, final ValueExpression terminator, final ValueExpression expectedValue, final Token expectedTail) {
        return seq(post(until("value", con(initial), con(step, signed()), con(max), def("terminator", 1, eq(terminator))), eq(last(ref("value")), expectedValue)), expectedTail);
    }

    private static Token untilToken(final int initial, final int step, final int max, final ValueExpression terminator, final ValueExpression expectedValue) {
        return post(until("value", con(initial), con(step, signed()), con(max), def("terminator", 1, eq(terminator))), eq(last(ref("value")), expectedValue));
    }

    private static Token untilToken(final ValueExpression initial, final ValueExpression step, final ValueExpression max, final ValueExpression terminator, final ValueExpression expectedValue) {
        return post(until("value", initial, step, max, def("terminator", 1, eq(terminator))), eq(last(ref("value")), expectedValue));
    }

    private static Token untilToken(final int initial, final int step, final ValueExpression terminator, final ValueExpression expectedValue, final Token expectedTail) {
        return seq(post(until("value", con(initial), con(step, signed()), def("terminator", 1, eq(terminator))), eq(last(ref("value")), expectedValue)), expectedTail);
    }

    private static Token untilToken(final int initial, final ValueExpression terminator, final ValueExpression expectedValue, final Token expectedTail) {
        return seq(post(until("value", con(initial), def("terminator", 1, eq(terminator))), eq(last(ref("value")), expectedValue)), expectedTail);
    }

    private static Token untilTokenAlwaysTrueTerminator(final int initial, final int step, final int max) {
        return until("value", con(initial), con(step, signed()), con(max), post(EMPTY, TRUE));
    }

}
