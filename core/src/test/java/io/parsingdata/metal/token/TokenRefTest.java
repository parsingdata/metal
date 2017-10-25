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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

public class TokenRefTest {

    @Test
    public void nonExistingRefToken() throws IOException {
        assertFalse(token("a").parse(stream(0), enc()).isPresent());
        assertFalse(seq("a", any("b"), token("c")).parse(stream(0), enc()).isPresent());
        assertFalse(sub(token("a"), con(0)).parse(stream(0), enc()).isPresent());
    }

    @Test
    public void findRightDefinition() throws IOException {
        // Clearly reference the second:
        assertTrue(createNamedTokens("out", "in", "in").parse(stream(21, 42), enc()).isPresent());
        assertTrue(createNamedTokens("out", "in", "in").parse(stream(21, 21, 42), enc()).isPresent());
        assertTrue(createNamedTokens("out", "in", "in").parse(stream(21, 21, 21, 42), enc()).isPresent());
        // Clearly reference the first:
        assertTrue(createNamedTokens("in", "out", "in").parse(stream(21, 42), enc()).isPresent());
        assertTrue(createNamedTokens("in", "out", "in").parse(stream(21, 42, 21, 42), enc()).isPresent());
        // Reference the first:
        assertTrue(createNamedTokens("in", "in", "in").parse(stream(21, 42), enc()).isPresent());
        assertTrue(createNamedTokens("in", "in", "in").parse(stream(21, 42, 21, 42), enc()).isPresent());
        // So that this will fail:
        assertFalse(createNamedTokens("in", "in", "in").parse(stream(21, 21, 42), enc()).isPresent());
    }

    private Token createNamedTokens(String firstSeq, String secondSeq, String refName) {
        return
            seq(firstSeq,
                seq(secondSeq,
                    def("value", 1, eq(con(21))),
                    opt(token(refName))
                ),
                def("footer", 1, eq(con(42)))
            );
    }

}
