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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;

public class PostTest {

    private static final Token SEQUENCE =
        post(seq(any("header"),
                 def("value", 1, eq(con(1)))),
                 eq(ref("header"), con(1)));

    @Test
    public void postconditionTrue() throws IOException {
        final Optional<Environment> result = SEQUENCE.parse(stream(1, 1), enc());

        // token parses and postcondition is true
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().offset, is(2L));
    }

    @Test
    public void postconditionFalse() throws IOException {
        final Optional<Environment> result = SEQUENCE.parse(stream(0, 1), enc());

        // token parses, but postcondition is false
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void postconditionParseFails() throws IOException {
        final Optional<Environment> result = SEQUENCE.parse(stream(1, 2), enc());

        // parse fails
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void testToString() {
        final Token simpleWhile = post("pname", def("value", con(1)), eq(con(1)));
        final String simpleWhileString = "Post(pname,Def(value,Const(0x01)), Eq(Const(0x01)))";
        assertThat(simpleWhile.toString(), is(equalTo(simpleWhileString)));
    }

}