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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class ErrorsTest {

    @Test
    public void noValueForSize() throws IOException {
        final Token t = def("a", div(con(10), con(0)));
        Assert.assertFalse(t.parse(stream(1), enc()).succeeded());
    }

    @Test(expected=IOException.class)
    public void ioError() throws IOException {
        final Token t = any("a");
        final ByteStream stream = new ByteStream() {
            @Override
            public int read(final long offset, final byte[] data) throws IOException { throw new IOException(); }
        };
        final Environment env = new Environment(stream);
        t.parse(env, enc());
    }

}
