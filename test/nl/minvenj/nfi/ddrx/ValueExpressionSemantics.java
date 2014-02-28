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
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.div;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.mul;
import static nl.minvenj.nfi.ddrx.Shorthand.neg;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import static nl.minvenj.nfi.ddrx.Shorthand.val;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.data.Environment.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.value.BinaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.NumericValue;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class ValueExpressionSemantics {

    private Token singleToken(String firstName, String secondName, ValueExpression<NumericValue> ve) {
        return seq(any(firstName),
                   val(secondName, con(1), eq(ref(secondName), ve)));
    }

    private Token binaryValueExpressionToken(BinaryValueExpression<NumericValue> bve) {
        return seq(any("a"),
                   singleToken("b", "c", bve));
    }

    private Token unaryValueExpressionToken(UnaryValueExpression<NumericValue> uve) {
        return singleToken("a", "b", uve);
    }

    @Test
    public void Add() {
        Token add = binaryValueExpressionToken(add(ref("a"), ref("b")));
        Assert.assertTrue(add.parse(stream(1, 2, 3)));
        Assert.assertTrue(add.parse(stream(-10, 3, -7)));
        Assert.assertTrue(add.parse(stream(-10, -8, -18)));
        Assert.assertTrue(add.parse(stream(10, -7, 3)));
        Assert.assertTrue(add.parse(stream(10, -25, -15)));
        Assert.assertFalse(add.parse(stream(1, 2, 4)));
    }

    @Test
    public void Div() {
        Token div = binaryValueExpressionToken(div(ref("a"), ref("b")));
        Assert.assertTrue(div.parse(stream(8, 2, 4)));
        Assert.assertTrue(div.parse(stream(1, 2, 0)));
        Assert.assertTrue(div.parse(stream(7, 8, 0)));
        Assert.assertTrue(div.parse(stream(3, 2, 1)));
        Assert.assertTrue(div.parse(stream(1, 1, 1)));
        Assert.assertFalse(div.parse(stream(4, 2, 1)));
    }

    @Test
    public void Mul() {
        Token mul = binaryValueExpressionToken(mul(ref("a"), ref("b")));
        Assert.assertTrue(mul.parse(stream(2, 2, 4)));
        Assert.assertTrue(mul.parse(stream(0, 42, 0)));
        Assert.assertTrue(mul.parse(stream(42, 0, 0)));
        Assert.assertTrue(mul.parse(stream(1, 1, 1)));
        Assert.assertTrue(mul.parse(stream(0, 0, 0)));
        Assert.assertFalse(mul.parse(stream(2, 3, 8)));
    }

    @Test
    public void Sub() {
        Token sub = binaryValueExpressionToken(sub(ref("a"), ref("b")));
        Assert.assertTrue(sub.parse(stream(8, 2, 6)));
        Assert.assertTrue(sub.parse(stream(3, 10, -7)));
        Assert.assertTrue(sub.parse(stream(0, 42, -42)));
        Assert.assertTrue(sub.parse(stream(-42, 10, -52)));
        Assert.assertTrue(sub.parse(stream(-42, -10, -32)));
        Assert.assertFalse(sub.parse(stream(-42, 42, 0)));
    }

    @Test
    public void Neg() {
        Token neg = unaryValueExpressionToken(neg(ref("a")));
        Assert.assertTrue(neg.parse(stream(1, -1)));
        Assert.assertTrue(neg.parse(stream(2, -2)));
        Assert.assertTrue(neg.parse(stream(-3, 3)));
        Assert.assertTrue(neg.parse(stream(0, 0)));
        Assert.assertFalse(neg.parse(stream(4, 4)));
        Assert.assertFalse(neg.parse(stream(-5, -5)));
    }

}
