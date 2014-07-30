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

import static nl.minvenj.nfi.ddrx.Shorthand.add;
import static nl.minvenj.nfi.ddrx.Shorthand.cat;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.eqNum;
import static nl.minvenj.nfi.ddrx.Shorthand.mul;
import static nl.minvenj.nfi.ddrx.Shorthand.reduce;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.le;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.Reducer;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class Reducers extends ParameterizedParse {

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[1, 2, 3, 6] a, a, a, addAll(a)", reduceAddA, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 7] a, a, a, addAll(a)", reduceAddA, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 3, 6] a, a, a, mulAll(a)", reduceMulA, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 7] a, a, a, mulAll(a)", reduceMulA, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 4, 15] a, a, a, (addAll(a)+mulAll(a))", reduceAllAplusMulA, stream(1, 2, 4, 15), enc(), true },
            { "[1, 2, 4, 16] a, a, a, (addAll(a)+mulAll(a))", reduceAllAplusMulA, stream(1, 2, 4, 16), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a)", reduceCatA, stream(1, 2, 3, 1, 2, 3), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a)", reduceCatA, stream(1, 2, 3, 3, 2, 1), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a) BE", reduceCatAToNumBE, stream(1, 2, 3, 1, 2, 3), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a) BE", reduceCatAToNumBE, stream(1, 2, 3, 3, 2, 1), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a) LE", reduceCatAToNumLE, stream(1, 2, 3, 1, 2, 3), enc(), false },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a) LE", reduceCatAToNumLE, stream(1, 2, 3, 3, 2, 1), enc(), true }
        });
    }

    public Reducers(String desc, Token token, Environment env, Encoding enc, boolean result) {
        super(token, env, enc, result);
    }
    
    private final static Reducer addReducer = new Reducer() { @Override public ValueExpression reduce(ValueExpression l, ValueExpression r) { return add(l, r); } };
    private final static Reducer mulReducer = new Reducer() { @Override public ValueExpression reduce(ValueExpression l, ValueExpression r) { return mul(l, r); } };
    private final static Reducer catReducer = new Reducer() { @Override public ValueExpression reduce(ValueExpression l, ValueExpression r) { return cat(l, r); } };

    private final static Token reduceAddA = token(1, eq(reduce("a", addReducer)));
    private final static Token reduceMulA = token(1, eq(reduce("a", mulReducer)));
    private final static Token reduceAllAplusMulA = token(1, eq(add(reduce("a", addReducer), reduce("a", mulReducer))));
    private final static Token reduceCatA = token(3, eq(reduce("a", catReducer)));
    private final static Token reduceCatAToNumBE = token(3, eqNum(reduce("a", catReducer)), enc());    
    private final static Token reduceCatAToNumLE = token(3, eqNum(reduce("a", catReducer)), le());    

    private static Token token(long size, Expression pred, Encoding enc) {
        return seq(any("a"),
                   any("a"),
                   any("a"),
                   def("b", con(size), pred, enc));
    }

    private static Token token(long size, Expression pred) {
        return token(size, pred, enc());
    }

}
