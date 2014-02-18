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

import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.stream;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.value.Add;
import nl.minvenj.nfi.ddrx.expression.value.BinaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

@RunWith(JUnit4.class)
public class ValueExpression {
    
    private Token binaryValueExpressionToken(BinaryValueExpression bve) {
        return new Sequence(
                            any("a"),
                            new Sequence(
                                         any("b"),
                                         new Value("c", new Con(BigInteger.valueOf(1)), new Equals(new Ref("c"), bve))));
    }
    
    @Test
    public void Add() {
        Token add = binaryValueExpressionToken(new Add(new Ref("a"), new Ref("b")));
        Assert.assertTrue(add.eval(stream(1, 2, 3)));
        Assert.assertFalse(add.eval(stream(1, 2, 4)));
    }
    
}
