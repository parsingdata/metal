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
import static nl.minvenj.nfi.metal.Shorthand.and;
import static nl.minvenj.nfi.metal.Shorthand.cat;
import static nl.minvenj.nfi.metal.Shorthand.cho;
import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.currentOffset;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.div;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.eqNum;
import static nl.minvenj.nfi.metal.Shorthand.eqStr;
import static nl.minvenj.nfi.metal.Shorthand.first;
import static nl.minvenj.nfi.metal.Shorthand.gtNum;
import static nl.minvenj.nfi.metal.Shorthand.ltNum;
import static nl.minvenj.nfi.metal.Shorthand.mod;
import static nl.minvenj.nfi.metal.Shorthand.mul;
import static nl.minvenj.nfi.metal.Shorthand.neg;
import static nl.minvenj.nfi.metal.Shorthand.nod;
import static nl.minvenj.nfi.metal.Shorthand.not;
import static nl.minvenj.nfi.metal.Shorthand.offset;
import static nl.minvenj.nfi.metal.Shorthand.opt;
import static nl.minvenj.nfi.metal.Shorthand.or;
import static nl.minvenj.nfi.metal.Shorthand.pre;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.rep;
import static nl.minvenj.nfi.metal.Shorthand.self;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.Shorthand.str;
import static nl.minvenj.nfi.metal.Shorthand.sub;
import static nl.minvenj.nfi.metal.Shorthand.whl;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import nl.minvenj.nfi.metal.expression.Expression;
import nl.minvenj.nfi.metal.expression.value.ValueExpression;
import nl.minvenj.nfi.metal.token.Token;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
        final Token t = sub(opt(pre(str("str", rep(cho(any(n()), seq(nod(v()), whl(def(n(), con(1), e), e))))), e)), v());
        final String output = t.toString();
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(output.contains(prefix + i));
        }
    }

    private String n() {
        return prefix + count++;
    }

    private ValueExpression v() {
        return neg(add(div(mod(mul(sub(ref(n()), first(n())), con(1)), cat(ref(n()), ref(n()))), add(self, add(offset(n()), currentOffset))), ref(n())));
    }

}
