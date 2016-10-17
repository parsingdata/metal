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

package io.parsingdata.metal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public class ErrorsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noValueForSize() throws IOException {
        thrown = ExpectedException.none();
        final Token token = def("a", div(con(10), con(0)));
        assertFalse(token.parse(stream(1), enc()).succeeded);
    }

    @Test
    public void ioError() throws IOException {
        thrown.expect(IOException.class);
        final Token token = any("a");
        final ByteStream stream = new ByteStream() {
            @Override
            public int read(final long offset, final byte[] data) throws IOException { throw new IOException(); }
        };
        final Environment environment = new Environment(stream);
        token.parse(environment, enc());
    }

    @Test
    public void multiValueInRepN() throws IOException {
        final Token dummy = any("a");
        final Token multiRepN =
            seq(any("b"),
                any("b"),
                repn(dummy, ref("b"))
            );
        ParseResult result = multiRepN.parse(stream(2, 2, 2, 2), enc());
        assertFalse(result.succeeded);
    }

    @Test
    public void definedValueHasNoOffset() {
        final OptionalValueList offsetCon = offset(con(1)).eval(stream(), enc());
        assertFalse(offsetCon.isEmpty());
        assertEquals(1, offsetCon.size);
        assertFalse(offsetCon.head.isPresent());
    }

}
