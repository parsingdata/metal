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
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;

public class PreTest {

    private static final Token PRECONDITION = pre(def("value", 1, eq(con(1))), eq(ref("header"), con(1)));
    private static final Token SEQUENCE = seq(def("header", 1), PRECONDITION);

    @Test
    public void preconditionTrue() throws IOException {
        final Optional<Environment> result = SEQUENCE.parse(stream(1, 1), enc());

        // precondition is true, token is parsed
        assertThat(result.get().offset.longValue(), is(2L));
    }

    @Test
    public void preconditionFalse() throws IOException {
        final Optional<Environment> result = SEQUENCE.parse(stream(0, 1), enc());

        // precondition is false, token is not parsed
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void preconditionTrueParseFails() throws IOException {
        final Optional<Environment> result = SEQUENCE.parse(stream(1, 2), enc());

        // precondition is true, but token can't be parsed
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void testToString() {
        final Token simpleWhile = pre("pname", def("value", con(1)), eq(con(1)));
        final String simpleWhileString = "Pre(pname,Def(value,Const(0x01)),Eq(Const(0x01)))";
        assertThat(simpleWhile.toString(), is(equalTo(simpleWhileString)));
    }

}