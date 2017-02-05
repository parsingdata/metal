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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.or;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.arithmetic.Add;
import io.parsingdata.metal.expression.value.arithmetic.Div;
import io.parsingdata.metal.expression.value.arithmetic.Mod;
import io.parsingdata.metal.expression.value.arithmetic.Mul;
import io.parsingdata.metal.expression.value.arithmetic.Sub;
import io.parsingdata.metal.expression.value.bitwise.And;
import io.parsingdata.metal.expression.value.bitwise.Or;
import io.parsingdata.metal.expression.value.bitwise.ShiftLeft;
import io.parsingdata.metal.expression.value.bitwise.ShiftRight;

@RunWith(Parameterized.class)
public class TwoValueExpressionEqualityTest {

    private final Constructor constructor;

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { And.class },
            { Or.class },
            { ShiftLeft.class },
            { ShiftRight.class },
            { Add.class },
            { Div.class },
            { Mod.class },
            { Mul.class },
            { Sub.class },
            { Cat.class },
            { Eq.class },
            { EqNum.class },
            { EqStr.class },
            { GtNum.class },
            { LtNum.class }
        });
    }

    public TwoValueExpressionEqualityTest(final Class<?> target) throws NoSuchMethodException {
        this.constructor = target.getConstructor(ValueExpression.class, ValueExpression.class);
    }

    @Test
    public void NotEqualsNull() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertFalse(makeTVE12().equals(null));
    }

    @Test
    public void equalsItselfIdentity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final Object obj = makeTVE12();
        assertTrue(obj.equals(obj));
    }

    @Test
    public void equalsItself() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertTrue(makeTVE12().equals(makeTVE12()));
    }

    @Test
    public void notEqualsRightLeft() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeTVE12().equals(makeTVE21()));
    }

    @Test
    public void notEqualsLeftLeft() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeTVE12().equals(makeTVE11()));
    }

    @Test
    public void notEqualsType() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeTVE12().equals(new BinaryValueExpression(makeTVE1(), makeTVE2()) { @Override public Optional<Value> eval(Value left, Value right, ParseGraph graph, Encoding encoding) { return null; } }));
        assertFalse(makeTVE12().equals(new ComparisonExpression(makeTVE1(), makeTVE2()) { @Override public boolean compare(Value left, Value right) { return false; } }));
    }

    @Test
    public void noHashCollisions() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertEquals(makeTVE11().hashCode(), makeTVE11().hashCode());
        assertEquals(makeTVE12().hashCode(), makeTVE12().hashCode());
        assertEquals(makeTVE21().hashCode(), makeTVE21().hashCode());
        assertNotEquals(makeTVE11().hashCode(), makeTVE12().hashCode());
        assertNotEquals(makeTVE12().hashCode(), makeTVE21().hashCode());
        assertNotEquals(makeTVE21().hashCode(), makeTVE11().hashCode());
    }

    private Object makeTVE12() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(makeTVE1(), makeTVE2());
    }

    private Object makeTVE21() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(makeTVE2(), makeTVE1());
    }

    private Object makeTVE11() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(makeTVE1(), makeTVE1());
    }

    private ValueExpression makeTVE1() {
        return and(con(1), con(2));
    }

    private ValueExpression makeTVE2() {
        return or(con(1), con(3));
    }

}
