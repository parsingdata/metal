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

package io.parsingdata.metal;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.TokenDefinitions.any;

@RunWith(JUnit4.class)
public class ToStringTest {

    private static final String prefix = "prefix";
    private int count;

    @Before
    public void before() {
        count = 0;
    }

    @Test
    public void validateToStringImplementation() {
        final Expression e = not(and(eq(v()), or(eqNum(v()), and(eqStr(v()), or(gtNum(v()), ltNum(v()))))));
        final Token t = repn(sub(opt(pre(str("str", rep(cho(any(n()), seq(nod(v()), whl(def(n(), con(1), e), e))))), e)), v()), v());
        final String output = t.toString();
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(output.contains(prefix + i));
        }
    }

    private String n() {
        return prefix + count++;
    }

    private Token t() { return any("a"); }

    private ValueExpression v() {
        return neg(add(div(mod(mul(sub(ref(n()), first(ref(n()))), con(1)), cat(ref(n()), ref(t()))), add(self, add(offset(ref(n())), add(currentOffset, count(ref(n())))))), elvis(ref(n()), ref(n()))));
    }

    @Test
    public void tokensWithArrays() {
        final Token a = def("_name_a_", con(1));
        final Token b = def("_name_b_", con(2));
        final Token c = def("_name_c_", con(1));
        final Token s1 = seq(a, b, c);
        checkToken(s1);
        final Token c1 = cho(c, b, a);
        checkToken(c1);
    }

    private void checkToken(final Token t) {
        final String s1s = t.toString();
        Assert.assertTrue(s1s.contains("_name_a_"));
        Assert.assertTrue(s1s.contains("_name_b_"));
        Assert.assertTrue(s1s.contains("_name_c_"));
    }

}
