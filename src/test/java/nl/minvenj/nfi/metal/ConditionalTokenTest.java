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

import static nl.minvenj.nfi.metal.Shorthand.add;
import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.currentOffset;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.eqNum;
import static nl.minvenj.nfi.metal.Shorthand.ltNum;
import static nl.minvenj.nfi.metal.Shorthand.offset;
import static nl.minvenj.nfi.metal.Shorthand.pre;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.Shorthand.whl;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;
import nl.minvenj.nfi.metal.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class ConditionalTokenTest extends ParameterizedParse {

    @Parameters(name = "{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[1, 2, 3] a b c", preToken, stream(1, 2, 3), enc(), true },
            { "[2, 3] a c", preToken, stream(2, 3), enc(), true },
            { "[1, 2, 2] a b c(error)", preToken, stream(1, 2, 2), enc(), false },
            { "[2, 2] a c(error)", preToken, stream(2, 2), enc(), false },
            { "[2, 1, 3, -1] a (a x any) -1", whileToken, stream(2, 1, 3, -1), enc(), true },
            { "[0, -1] a (a x any) -1", whileToken, stream(0, -1), enc(), true },
            { "[2, -1, -1, 0, -1, -1] a (a x any) -1(error)", whileToken, stream(2, -1, -1, 0, -1, -1), enc(), false },
            { "[0, 0, -1] a (a x any) -1(error)", whileToken, stream(0, 0, -1), enc(), false }
        });
    }

    public ConditionalTokenTest(final String name, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static final Token preToken = seq(any("a"),
                                              pre(any("b"), eqNum(ref("a"), con(1))),
                                              def("c", con(1), eqNum(con(3))));
    
    private static final Token whileToken = seq(any("size"),
                                                whl(any("value"), ltNum(currentOffset, add(ref("size"), add(offset("size"), con(1))))),
                                                def("footer", con(1), eq(con(0xff))));

}
