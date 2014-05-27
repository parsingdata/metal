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

import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.eqRef;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class NameBind extends ParameterizedParse {

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[0x2a, 0x2a] b == a", sequenceMatch2, stream(42, 42), enc(), true },
            { "[0x2a, 0x15] b == a", sequenceMatch2, stream(42, 21), enc(), false },
            { "[0x2a, 0x2a, 0x2a] b == a, c == a", sequenceMatch3, stream(42, 42, 42), enc(), true },
            { "[0x2a, 0x2a, 0x15] b == a, c == a", sequenceMatch3, stream(42, 42, 21), enc(), false },
            { "[0x2a, 0x15, 0x2a] b == a, c == a", sequenceMatch3, stream(42, 21, 42), enc(), false },
            { "[0x15, 0x2a, 0x2a] b == a, c == a", sequenceMatch3, stream(21, 42, 42), enc(), false },
            { "[0x15, 0x2a, 0x3f] b == a, c == a", sequenceMatch3, stream(21, 42, 63), enc(), false },
            { "[0x2a, 0x2a, 0x2a] b == a, c == b", sequenceMatchTransitive3, stream(42, 42, 42), enc(), true },
            { "[0x2a, 0x2a, 0x15] b == a, c == b", sequenceMatchTransitive3, stream(42, 42, 21), enc(), false },
            { "[0x2a, 0x15, 0x2a] b == a, c == b", sequenceMatchTransitive3, stream(42, 21, 42), enc(), false },
            { "[0x15, 0x2a, 0x2a] b == a, c == b", sequenceMatchTransitive3, stream(21, 42, 42), enc(), false },
            { "[0x15, 0x2a, 0x63] b == a, c == b", sequenceMatchTransitive3, stream(21, 42, 63), enc(), false }
        });
    }

    public NameBind(String desc, Token token, Environment env, Encoding enc, boolean result) {
        super(token, env, enc, result);
    }

    private static Token sequenceMatch2 = seq(any("a"),
                                              eqRef("b", "a"));
    private static Token sequenceMatch3 = seq(sequenceMatch2,
                                              eqRef("c", "a"));
    private static Token sequenceMatchTransitive3 = seq(sequenceMatch2,
                                                        eqRef("c", "b"));

}
