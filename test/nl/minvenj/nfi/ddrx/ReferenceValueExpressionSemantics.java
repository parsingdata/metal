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
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.first;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.eqRef;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class ReferenceValueExpressionSemantics extends ParameterizedParse {

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
            { "[1, 2, 1] a, a, first(a)", refList(first("a")), stream(1, 2, 1), enc(), true },
            { "[1, 2, 3] a, a, first(a)", refList(first("a")), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3] a, a, first(b)", refList(first("b")), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3] a, a, ref(b)", refList(ref("b")), stream(1, 2, 3), enc(), false }
        });
    }

    public ReferenceValueExpressionSemantics(String desc, Token token, Environment env, Encoding enc, boolean result) {
        super(token, env, enc, result);
    }

    private static Token sequenceMatch2 = seq(any("a"),
                                              eqRef("b", "a"));
    private static Token sequenceMatch3 = seq(sequenceMatch2,
                                              eqRef("c", "a"));
    private static Token sequenceMatchTransitive3 = seq(sequenceMatch2,
                                                        eqRef("c", "b"));

    private static Token refList(ValueExpression exp) {
        return seq(any("a"),
                   any("a"),
                   def("z", con(1), eq(exp)));
    }

}
