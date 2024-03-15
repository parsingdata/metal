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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseState;

public class WhileTest {

    public static final Token VALUES = seq(
            def("value", con(1)),
            def("value2", con(1), gtNum(con(8)))
    );
    private static final Token WHILE = seq(VALUES, whl(VALUES, ltNum(ref("value"), con(1))));

    @Test
    public void parseAll() {
        // two sequences of two bytes would be parsed: [0,9] and [1,10]
        // the while stops because the second 'value' is >= 1
        final Optional<ParseState> result = WHILE.parse(env(stream(0, 9, 1, 10, 2, 11)));

        assertThat(result.get().offset.longValueExact(), is(4L));
    }

    @Test
    public void parseFails() {
        final Optional<ParseState> result = WHILE.parse(env(stream(0, 9, 0, 8)));

        // parsing fails because the nested token couldn't be parsed ('value2' <= 9)
        assertFalse(result.isPresent());
    }

    @Test
    public void whileWithoutExpression() {
        // passing null as predicate make this a while(true):
        final Token trueWhile = whl(def("value", 1), null, enc());
        final Optional<ParseState> result = trueWhile.parse(env(stream(0)));

        // parsing fails because the nested def fails at the end of the stream
        assertFalse(result.isPresent());
    }

    @Test
    public void testToString() {
        final Token simpleWhile = whl(def("value", con(1)), eq(con(1)));
        final String simpleWhileString = "While(Def(value,Const(0x01)),Eq(Const(0x01)))";
        assertThat(simpleWhile.toString(), is(equalTo(simpleWhileString)));
    }

}
