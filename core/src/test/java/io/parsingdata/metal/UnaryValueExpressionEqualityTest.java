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
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.arithmetic.Neg;
import io.parsingdata.metal.expression.value.bitwise.Not;
import io.parsingdata.metal.expression.value.reference.Len;
import io.parsingdata.metal.expression.value.reference.Offset;

@RunWith(Parameterized.class)
public class UnaryValueExpressionEqualityTest {

    private final Class target;
    private final Constructor constructor;

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { Len.class },
            { Offset.class },
            { Neg.class },
            { Not.class }
        });
    }

    public UnaryValueExpressionEqualityTest(final Class<UnaryValueExpression> target) throws NoSuchMethodException {
        this.target = target;
        this.constructor = target.getConstructor(ValueExpression.class);
    }

    @Test
    public void NotEqualsNull() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertFalse(makeUVE1().equals(null));
    }

    @Test
    public void equalsItselfIdentity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final UnaryValueExpression uve = makeUVE1();
        assertTrue(uve.equals(uve));
    }

    @Test
    public void equalsItself() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertTrue(makeUVE1().equals(makeUVE1()));
    }

    @Test
    public void notEquals() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeUVE1().equals(makeUVE2()));
    }

    @Test
    public void notEqualsType() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeUVE1().equals(new UnaryValueExpression(and(con(1), con(2))) { @Override public Optional<Value> eval(Value operand, ParseGraph graph, Encoding encoding) { return null; } }));
    }

    private UnaryValueExpression makeUVE1() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (UnaryValueExpression) constructor.newInstance(and(con(1), con(2)));
    }

    private UnaryValueExpression makeUVE2() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return (UnaryValueExpression) constructor.newInstance(or(con(1), con(2)));
    }

}
