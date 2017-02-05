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

import static org.junit.Assert.assertFalse;
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
public class BinaryValueExpressionEqualityTest {

    private final Class target;
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
            { Cat.class }
        });
    }

    public BinaryValueExpressionEqualityTest(final Class<BinaryValueExpression> target) throws NoSuchMethodException {
        this.target = target;
        this.constructor = target.getConstructor(ValueExpression.class, ValueExpression.class);
    }

    @Test
    public void NotEqualsNull() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertFalse(makeBVE12().equals(null));
    }

    @Test
    public void equalsItselfIdentity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final BinaryValueExpression bve = makeBVE12();
        assertTrue(bve.equals(bve));
    }

    @Test
    public void equalsItself() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertTrue(makeBVE12().equals(makeBVE12()));
    }

    @Test
    public void notEqualsRightLeft() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeBVE12().equals(makeBVE21()));
    }

    @Test
    public void notEqualsLeftLeft() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeBVE12().equals(makeBVE11()));
    }

    @Test
    public void notEqualsType() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeBVE12().equals(new BinaryValueExpression(makeBVE1(), makeBVE2()) { @Override public Optional<Value> eval(Value left, Value right, ParseGraph graph, Encoding encoding) { return null; } }));
    }

    private BinaryValueExpression makeBVE12() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (BinaryValueExpression) constructor.newInstance(makeBVE1(), makeBVE2());
    }

    private BinaryValueExpression makeBVE21() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (BinaryValueExpression) constructor.newInstance(makeBVE2(), makeBVE1());
    }

    private BinaryValueExpression makeBVE11() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (BinaryValueExpression) constructor.newInstance(makeBVE1(), makeBVE1());
    }

    private ValueExpression makeBVE1() {
        return and(con(1), con(2));
    }

    private ValueExpression makeBVE2() {
        return or(con(1), con(2));
    }

}
