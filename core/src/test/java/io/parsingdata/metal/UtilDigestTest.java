/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static java.lang.System.arraycopy;
import static java.security.MessageDigest.getInstance;

import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Util.digest;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EnvironmentFactory.env;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

@RunWith(Parameterized.class)
public class UtilDigestTest {

    @Parameter
    public int length;

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return List.of(new Object[][]{{11}, {512}, {512 + 1}, {1024}, {2048 + 1}});
    }

    @Test
    public void testLargeDigest() throws Exception {
        final byte[] content = new byte[length];
        new SecureRandom().nextBytes(content);

        final MessageDigest digester = getInstance("SHA-256");

        final byte[] data = new byte[length + 32];
        arraycopy(content, 0, data, 0, length);
        arraycopy(digester.digest(content), 0, data, length, 32);

        final Token token = seq("data",
            def("content", length),
            def("digest", 32, eq(digest("SHA-256", last(ref("content"))))));

        assertTrue(token.parse(env(createFromByteStream(new InMemoryByteStream(data)))).isPresent());
    }
}
