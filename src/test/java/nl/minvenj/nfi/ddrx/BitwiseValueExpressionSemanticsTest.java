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

import static nl.minvenj.nfi.ddrx.Shorthand.and;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

public class BitwiseValueExpressionSemanticsTest extends ParameterizedParse {

    @Parameters(name = "{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[170, 85] a, not(a)", simpleNot(1), stream(170, 85), enc(), true },
            { "[170, 85, 85, 170] a, not(a)", simpleNot(2), stream(170, 85, 85, 170), enc(), true },
            { "[170, 85, 0] a b and(a, b)", simpleAnd(1), stream(170, 85, 0), enc(), true },
            { "[170, 85, 85, 170, 0, 0] a b and(a, b)", simpleAnd(2), stream(170, 85, 85, 170, 0, 0), enc(), true },
        });
    }

    public BitwiseValueExpressionSemanticsTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static Token simpleNot(final int size) {
        return seq(def("a", con(size)), def("not(a)", con(size), eq(not(ref("a")))));
    }

    private static Token simpleAnd(final int size) {
        return
        seq(def("a", con(size)),
            def("b", con(size)),
            def("and(a, b)", con(size), eq(and(ref("a"), ref("b")))));
    }

}
