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
import static nl.minvenj.nfi.ddrx.Shorthand.defNum;
import static nl.minvenj.nfi.ddrx.Shorthand.gt;
import static nl.minvenj.nfi.ddrx.Shorthand.lt;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.refNum;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.data.Environment.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.ddrx.expression.value.NumericValue;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class ComparisonExpressionSemantics {
    
    private Token numComparison(ComparisonExpression<NumericValue> comparison) {
        return seq(any("a"),
                   defNum("b", con(1), comparison));
    }
    
    @Test
    public void Eq() {
        Token eq = numComparison(eq(refNum("a")));
        Assert.assertTrue(eq.parse(stream(1, 1)));
        Assert.assertFalse(eq.parse(stream(1, 2)));
    }
    
    @Test
    public void Gt() {
        Token eq = numComparison(gt(refNum("a")));
        Assert.assertFalse(eq.parse(stream(1, 1)));
        Assert.assertTrue(eq.parse(stream(1, 2)));
        Assert.assertFalse(eq.parse(stream(2, 1)));
    }
    
    @Test
    public void Lt() {
        Token eq = numComparison(lt(refNum("a")));
        Assert.assertFalse(eq.parse(stream(1, 1)));
        Assert.assertFalse(eq.parse(stream(1, 2)));
        Assert.assertTrue(eq.parse(stream(2, 1)));
    }
    
}
