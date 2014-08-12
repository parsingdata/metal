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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.div;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;

import nl.minvenj.nfi.ddrx.data.ByteStream;
import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Errors {

    @Test
    public void noValueForSize() throws IOException {
        Token t = def("a", div(con(10), con(0)));
        Assert.assertFalse(t.parse(stream(1), enc()));
    }

    @Test(expected=IOException.class)
    public void ioError() throws IOException {
        Token t = any("a");
        ByteStream stream = new ByteStream() {

            @Override
            public void mark() {}

            @Override
            public void reset() {}

            @Override
            public void clear() {}

            @Override
            public int read(byte[] data) throws IOException { throw new IOException(); }

            @Override
            public long offset() { return 0L; }

        };
        Environment env = new Environment(stream);
        t.parse(env, enc());
    }

}
