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
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.first;
import static nl.minvenj.nfi.metal.Shorthand.offset;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import static nl.minvenj.nfi.metal.TokenDefinitions.eqRef;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.value.ValueExpression;
import nl.minvenj.nfi.metal.token.Token;
import nl.minvenj.nfi.metal.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class ReferenceValueExpressionSemanticsTest extends ParameterizedParse {

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[2, 2] b == a", sequenceMatch2, stream(2, 2), enc(), true },
            { "[2, 1] b == a", sequenceMatch2, stream(2, 1), enc(), false },
            { "[2, 2] b == a, c == a", sequenceMatch3, stream(2, 2, 2), enc(), true },
            { "[2, 2, 1] b == a, c == a", sequenceMatch3, stream(2, 2, 1), enc(), false },
            { "[2, 1, 2] b == a, c == a", sequenceMatch3, stream(2, 1, 2), enc(), false },
            { "[1, 2, 2] b == a, c == a", sequenceMatch3, stream(1, 2, 2), enc(), false },
            { "[1, 2, 3] b == a, c == a", sequenceMatch3, stream(1, 2, 3), enc(), false },
            { "[2, 2, 2] b == a, c == b", sequenceMatchTransitive3, stream(2, 2, 2), enc(), true },
            { "[2, 2, 1] b == a, c == b", sequenceMatchTransitive3, stream(2, 2, 1), enc(), false },
            { "[2, 1, 2] b == a, c == b", sequenceMatchTransitive3, stream(2, 1, 2), enc(), false },
            { "[1, 2, 2] b == a, c == b", sequenceMatchTransitive3, stream(1, 2, 2), enc(), false },
            { "[1, 2, 3] b == a, c == b", sequenceMatchTransitive3, stream(1, 2, 3), enc(), false },
            { "[1, 2, 1] a, a, first(a)", refList("a", "a", first("a")), stream(1, 2, 1), enc(), true },
            { "[1, 2, 3] a, a, first(a)", refList("a", "a", first("a")), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3] a, a, first(b)", refList("a", "a", first("b")), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3] a, a, ref(b)", refList("a", "a", ref("b")), stream(1, 2, 3), enc(), false },
            { "[1, 2, 0] a, b, offset(a)", refList("a", "b", offset("a")), stream(1, 2, 0), enc(), true },
            { "[1, 2, 1] a, a, offset(a)", refList("a", "a", offset("a")), stream(1, 2, 1), enc(), true },
            { "[1, 2, 2] a, b, offset(z)", refList("a", "b", offset("z")), stream(1, 2, 2), enc(), true },
            { "[1, 2, 3] a, b, offset(c)", refList("a", "b", offset("c")), stream(1, 2, 3), enc(), false }
        });
    }

    public ReferenceValueExpressionSemanticsTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static final Token sequenceMatch2 = seq(any("a"),
                                                    eqRef("b", "a"));
    private static final Token sequenceMatch3 = seq(sequenceMatch2,
                                                    eqRef("c", "a"));
    private static final Token sequenceMatchTransitive3 = seq(sequenceMatch2,
                                                              eqRef("c", "b"));

    private static Token refList(final String first, final String second, final ValueExpression exp) {
        return seq(any(first),
                   any(second),
                   def("z", con(1), eq(exp)));
    }

}
