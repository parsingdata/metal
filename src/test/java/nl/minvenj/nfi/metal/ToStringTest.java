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
import static nl.minvenj.nfi.ddrx.Shorthand.and;
import static nl.minvenj.nfi.ddrx.Shorthand.cat;
import static nl.minvenj.nfi.ddrx.Shorthand.cho;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.div;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.eqNum;
import static nl.minvenj.nfi.ddrx.Shorthand.eqStr;
import static nl.minvenj.nfi.ddrx.Shorthand.first;
import static nl.minvenj.nfi.ddrx.Shorthand.gtNum;
import static nl.minvenj.nfi.ddrx.Shorthand.ltNum;
import static nl.minvenj.nfi.ddrx.Shorthand.mod;
import static nl.minvenj.nfi.ddrx.Shorthand.mul;
import static nl.minvenj.nfi.ddrx.Shorthand.neg;
import static nl.minvenj.nfi.ddrx.Shorthand.nod;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.opt;
import static nl.minvenj.nfi.ddrx.Shorthand.or;
import static nl.minvenj.nfi.ddrx.Shorthand.pre;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.self;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.str;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;

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
        final Token t = sub(opt(pre(str("str", rep(cho(any(n()), seq(nod(v()), def(n(), con(1), e))))), e)), v());
        final String output = t.toString();
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(output.contains(prefix + i));
        }
    }

    private String n() {
        return prefix + count++;
    }

    private ValueExpression v() {
        return neg(add(div(mod(mul(sub(ref(n()), first(n())), con(1)), cat(ref(n()), ref(n()))), self), ref(n())));
    }

}
