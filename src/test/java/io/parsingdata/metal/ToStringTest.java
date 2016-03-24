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

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.currentOffset;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.TokenDefinitions.any;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

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

    private ValueExpression v() {
        return neg(add(div(mod(mul(sub(ref(n()), first(n())), con(1)), cat(ref(n()), ref(n()))), add(self, add(offset(n()), currentOffset))), ref(n())));
    }

}
