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
import static nl.minvenj.nfi.metal.Shorthand.cat;
import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.eqNum;
import static nl.minvenj.nfi.metal.Shorthand.foldLeft;
import static nl.minvenj.nfi.metal.Shorthand.foldRight;
import static nl.minvenj.nfi.metal.Shorthand.mul;
import static nl.minvenj.nfi.metal.Shorthand.fold;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.Shorthand.sub;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EncodingFactory.le;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;
import nl.minvenj.nfi.metal.expression.value.Reducer;
import nl.minvenj.nfi.metal.expression.value.ValueExpression;
import nl.minvenj.nfi.metal.token.Token;
import nl.minvenj.nfi.metal.util.ParameterizedParse;

public class ReducersTest extends ParameterizedParse {

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[1, 2, 3, 6] a, a, a, addAll(a)", reduceAddA, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 6] a, a, a, addAll(a, 0)", reduceAddAInit0, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 6] a, a, a, addAll(a, 1)", reduceAddAInit1, stream(1, 2, 3, 6), enc(), false },
            { "[1, 2, 3, 7] a, a, a, addAll(a)", reduceAddA, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 3, 7] a, a, a, addAll(a, 0)", reduceAddAInit0, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 3, 7] a, a, a, addAll(a, 1)", reduceAddAInit1, stream(1, 2, 3, 7), enc(), true },
            { "[1, 2, 3, 6] a, a, a, mulAll(a)", reduceMulA, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 7] a, a, a, mulAll(a)", reduceMulA, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 4, 15] a, a, a, (addAll(a)+mulAll(a))", reduceAllAplusMulA, stream(1, 2, 4, 15), enc(), true },
            { "[1, 2, 4, 16] a, a, a, (addAll(a)+mulAll(a))", reduceAllAplusMulA, stream(1, 2, 4, 16), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a)", reduceCatA, stream(1, 2, 3, 1, 2, 3), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a)", reduceCatA, stream(1, 2, 3, 3, 2, 1), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a) BE", reduceCatAToNumBE, stream(1, 2, 3, 1, 2, 3), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a) BE", reduceCatAToNumBE, stream(1, 2, 3, 3, 2, 1), enc(), false },
            { "[3, 2, 1, 3, 2, 1] a, a, a, catAll(a) LE", reduceCatAToNumLE, stream(3, 2, 1, 3, 2, 1), enc(), true },
            { "[3, 2, 1, 1, 2, 3] a, a, a, catAll(a) LE", reduceCatAToNumLE, stream(3, 2, 1, 1, 2, 3), enc(), false },
            { "[10, 3, 2, 5] a, a, a, subAll(a) left", foldLeftSubA, stream(10, 3, 2, 5), enc(), true },
            { "[10, 3, 2, -13] a, a, a, subAll(a, 2) left", foldLeftSubAInit2, stream(10, 3, 2, -13), enc(), true },
            { "[10, 3, 2, 9] a, a, a, subAll(a) right", foldRightSubA, stream(10, 3, 2, 9), enc(), true },
            { "[10, 3, 2, 7] a, a, a, subAll(a, 2) right", foldRightSubAInit2, stream(10, 3, 2, 7), enc(), true }
        });
    }

    public ReducersTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private final static Reducer addReducer = new Reducer() { @Override public ValueExpression reduce(final ValueExpression l, final ValueExpression r) { return add(l, r); } };
    private final static Reducer mulReducer = new Reducer() { @Override public ValueExpression reduce(final ValueExpression l, final ValueExpression r) { return mul(l, r); } };
    private final static Reducer catReducer = new Reducer() { @Override public ValueExpression reduce(final ValueExpression l, final ValueExpression r) { return cat(l, r); } };
    private final static Reducer subReducer = new Reducer() { @Override public ValueExpression reduce(final ValueExpression l, final ValueExpression r) { return sub(l, r); } };

    private final static Token reduceAddA = token(1, eq(fold("a", addReducer)));
    private final static Token reduceAddAInit0 = token(1, eq(fold("a", addReducer, con(0))));
    private final static Token reduceAddAInit1 = token(1, eq(fold("a", addReducer, con(1))));
    private final static Token reduceMulA = token(1, eq(fold("a", mulReducer)));
    private final static Token reduceAllAplusMulA = token(1, eq(add(fold("a", addReducer), fold("a", mulReducer))));
    private final static Token reduceCatA = token(3, eq(fold("a", catReducer)));
    private final static Token reduceCatAToNumBE = token(3, eqNum(fold("a", catReducer)), enc());
    private final static Token reduceCatAToNumLE = token(3, eqNum(fold("a", catReducer)), le());
    private final static Token foldLeftSubA = token(1, eq(foldLeft("a", subReducer)));
    private final static Token foldLeftSubAInit2 = token(1, eq(foldLeft("a", subReducer, con(2))));
    private final static Token foldRightSubA = token(1, eq(foldRight("a", subReducer)));
    private final static Token foldRightSubAInit2 = token(1, eq(foldRight("a", subReducer, con(2))));

    private static Token token(final long size, final Expression pred, final Encoding enc) {
        return seq(any("a"),
                   any("a"),
                   any("a"),
                   def("b", con(size), pred, enc));
    }

    private static Token token(final long size, final Expression pred) {
        return token(size, pred, enc());
    }

}
