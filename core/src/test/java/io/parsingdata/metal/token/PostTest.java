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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseState;

public class PostTest {

    private static final Token SEQUENCE =
        post(seq(any("header"),
                 def("value", 1, eq(con(1)))),
                 eq(ref("header"), con(1)));

    @Test
    public void postconditionTrue() {
        final Optional<ParseState> result = SEQUENCE.parse(env(stream(1, 1)));

        // token parses and postcondition is true
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().offset.longValueExact(), is(2L));
    }

    @Test
    public void postconditionFalse() {
        final Optional<ParseState> result = SEQUENCE.parse(env(stream(0, 1)));

        // token parses, but postcondition is false
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void postconditionParseFails() {
        final Optional<ParseState> result = SEQUENCE.parse(env(stream(1, 2)));

        // parse fails
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void testToString() {
        final Token simpleWhile = post("pname", def("value", con(1)), eq(con(1)));
        final String simpleWhileString = "Post(pname,Def(value,Const(0x01)),Eq(Const(0x01)))";
        assertThat(simpleWhile.toString(), is(equalTo(simpleWhileString)));
    }

}