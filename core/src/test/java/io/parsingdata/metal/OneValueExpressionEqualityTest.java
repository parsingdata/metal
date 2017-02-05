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

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.arithmetic.Neg;
import io.parsingdata.metal.expression.value.bitwise.Not;
import io.parsingdata.metal.expression.value.reference.Count;
import io.parsingdata.metal.expression.value.reference.First;
import io.parsingdata.metal.expression.value.reference.Last;
import io.parsingdata.metal.expression.value.reference.Len;
import io.parsingdata.metal.expression.value.reference.Offset;

@RunWith(Parameterized.class)
public class OneValueExpressionEqualityTest {

    private final Constructor constructor;

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { Len.class },
            { Offset.class },
            { Neg.class },
            { Not.class },
            { Count.class },
            { First.class },
            { Last.class }
        });
    }

    public OneValueExpressionEqualityTest(final Class<?> target) throws NoSuchMethodException {
        this.constructor = target.getConstructor(ValueExpression.class);
    }

    @Test
    public void NotEqualsNull() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertFalse(makeOVE1().equals(null));
    }

    @Test
    public void equalsItselfIdentity() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final Object obj = makeOVE1();
        assertTrue(obj.equals(obj));
    }

    @Test
    public void equalsItself() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertTrue(makeOVE1().equals(makeOVE1()));
    }

    @Test
    public void notEquals() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeOVE1().equals(makeOVE2()));
    }

    @Test
    public void notEqualsType() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertFalse(makeOVE1().equals(new UnaryValueExpression(and(con(1), con(2))) { @Override public Optional<Value> eval(Value operand, ParseGraph graph, Encoding encoding) { return null; } }));
        assertFalse(makeOVE1().equals(new ValueExpression() { @Override public ImmutableList<Optional<Value>> eval(ParseGraph graph, Encoding encoding) { return null; } }));
    }

    @Test
    public void noHashCollisions() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        assertEquals(makeOVE1().hashCode(), makeOVE1().hashCode());
        assertEquals(makeOVE2().hashCode(), makeOVE2().hashCode());
        assertNotEquals(makeOVE1().hashCode(), makeOVE2().hashCode());
    }

    private Object makeOVE1() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(and(con(1), con(2)));
    }

    private Object makeOVE2() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(or(con(1), con(3)));
    }

}
