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
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.val;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static nl.minvenj.nfi.ddrx.data.Environment.stream;
import nl.minvenj.nfi.ddrx.expression.value.Add;
import nl.minvenj.nfi.ddrx.expression.value.BinaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Div;
import nl.minvenj.nfi.ddrx.expression.value.Mul;
import nl.minvenj.nfi.ddrx.expression.value.Neg;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.expression.value.Sub;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class ValueExpressionSemantics {

    private Token singleToken(String firstName, String secondName, ValueExpression ve) {
        return seq(any(firstName),
                   val(secondName, con(1), eq(ref(secondName), ve)));
    }

    private Token binaryValueExpressionToken(BinaryValueExpression bve) {
        return seq(any("a"),
                   singleToken("b", "c", bve));
    }

    private Token unaryValueExpressionToken(UnaryValueExpression uve) {
        return singleToken("a", "b", uve);
    }

    @Test
    public void Add() {
        Token add = binaryValueExpressionToken(new Add(new Ref("a"), new Ref("b")));
        Assert.assertTrue(add.eval(stream(1, 2, 3)));
        Assert.assertTrue(add.eval(stream(-10, 3, -7)));
        Assert.assertTrue(add.eval(stream(-10, -8, -18)));
        Assert.assertTrue(add.eval(stream(10, -7, 3)));
        Assert.assertTrue(add.eval(stream(10, -25, -15)));
        Assert.assertFalse(add.eval(stream(1, 2, 4)));
    }

    @Test
    public void Div() {
        Token div = binaryValueExpressionToken(new Div(new Ref("a"), new Ref("b")));
        Assert.assertTrue(div.eval(stream(8, 2, 4)));
        Assert.assertTrue(div.eval(stream(1, 2, 0)));
        Assert.assertTrue(div.eval(stream(7, 8, 0)));
        Assert.assertTrue(div.eval(stream(3, 2, 1)));
        Assert.assertTrue(div.eval(stream(1, 1, 1)));
        Assert.assertFalse(div.eval(stream(4, 2, 1)));
    }

    @Test
    public void Mul() {
        Token mul = binaryValueExpressionToken(new Mul(new Ref("a"), new Ref("b")));
        Assert.assertTrue(mul.eval(stream(2, 2, 4)));
        Assert.assertTrue(mul.eval(stream(0, 42, 0)));
        Assert.assertTrue(mul.eval(stream(42, 0, 0)));
        Assert.assertTrue(mul.eval(stream(1, 1, 1)));
        Assert.assertTrue(mul.eval(stream(0, 0, 0)));
        Assert.assertFalse(mul.eval(stream(2, 3, 8)));
    }

    @Test
    public void Sub() {
        Token sub = binaryValueExpressionToken(new Sub(new Ref("a"), new Ref("b")));
        Assert.assertTrue(sub.eval(stream(8, 2, 6)));
        Assert.assertTrue(sub.eval(stream(3, 10, -7)));
        Assert.assertTrue(sub.eval(stream(0, 42, -42)));
        Assert.assertTrue(sub.eval(stream(-42, 10, -52)));
        Assert.assertTrue(sub.eval(stream(-42, -10, -32)));
        Assert.assertFalse(sub.eval(stream(-42, 42, 0)));
    }

    @Test
    public void Neg() {
        Token neg = unaryValueExpressionToken(new Neg(new Ref("a")));
        Assert.assertTrue(neg.eval(stream(1, -1)));
        Assert.assertTrue(neg.eval(stream(2, -2)));
        Assert.assertTrue(neg.eval(stream(-3, 3)));
        Assert.assertTrue(neg.eval(stream(0, 0)));
        Assert.assertFalse(neg.eval(stream(4, 4)));
        Assert.assertFalse(neg.eval(stream(-5, -5)));
    }

}
