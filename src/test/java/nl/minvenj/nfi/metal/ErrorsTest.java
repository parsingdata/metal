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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.div;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.metal.data.ByteStream;
import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParsedValueList;
import nl.minvenj.nfi.metal.token.Token;

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
        final Environment env = new Environment(ParsedValueList.EMPTY, stream, 0L);
        t.parse(env, enc());
    }

}
