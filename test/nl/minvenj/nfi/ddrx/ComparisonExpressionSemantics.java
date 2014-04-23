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
import static nl.minvenj.nfi.ddrx.Shorthand.expTrue;
import static nl.minvenj.nfi.ddrx.Shorthand.gt;
import static nl.minvenj.nfi.ddrx.Shorthand.lt;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.data.Environment.stream;
import nl.minvenj.nfi.ddrx.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ComparisonExpressionSemantics {

    private Token numCom(int size, ComparisonExpression comparison) {
        return seq(any("a"),
                   def("b", con(size), comparison));
    }

    private Token strCom(int size, ComparisonExpression comparison) {
        return seq(def("a", con(size), expTrue()),
                   def("b", con(size), comparison));
    }

    private Token valCom(int size, ComparisonExpression comparison) {
        return seq(def("a", con(size), expTrue()),
                   def("b", con(size), comparison));
    }

    @Test
    public void EqNum() {
        Token eq = numCom(1, eq(ref("a")));
        Assert.assertTrue(eq.parse(stream(1, 1)));
        Assert.assertFalse(eq.parse(stream(1, 2)));
    }

    @Test
    public void Gt() {
        Token eq = numCom(1, gt(ref("a")));
        Assert.assertFalse(eq.parse(stream(1, 1)));
        Assert.assertTrue(eq.parse(stream(1, 2)));
        Assert.assertFalse(eq.parse(stream(2, 1)));
    }

    @Test
    public void Lt() {
        Token eq = numCom(1, lt(ref("a")));
        Assert.assertFalse(eq.parse(stream(1, 1)));
        Assert.assertFalse(eq.parse(stream(1, 2)));
        Assert.assertTrue(eq.parse(stream(2, 1)));
    }

    @Test
    public void EqStr() {
        String a = "abc";
        String b = "abd";
        Token eq = strCom(a.length(), eq(ref("a")));
        Assert.assertTrue(eq.parse(stream(a + a)));
        Assert.assertFalse(eq.parse(stream(a + b)));
    }

    @Test
    public void EqVal() {
        Token eq = valCom(1, eq(ref("a")));
        Assert.assertTrue(eq.parse(stream(1, 1)));
        Assert.assertFalse(eq.parse(stream(1, 2)));
    }

}
