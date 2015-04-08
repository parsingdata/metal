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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;
import nl.minvenj.nfi.metal.expression.comparison.Eq;
import nl.minvenj.nfi.metal.expression.logical.And;
import nl.minvenj.nfi.metal.expression.logical.Not;
import nl.minvenj.nfi.metal.expression.value.Cat;
import nl.minvenj.nfi.metal.expression.value.FoldLeft;
import nl.minvenj.nfi.metal.expression.value.FoldRight;
import nl.minvenj.nfi.metal.expression.value.Reducer;
import nl.minvenj.nfi.metal.expression.value.ValueExpression;
import nl.minvenj.nfi.metal.expression.value.arithmetic.Neg;
import nl.minvenj.nfi.metal.expression.value.reference.First;
import nl.minvenj.nfi.metal.expression.value.reference.Offset;
import nl.minvenj.nfi.metal.expression.value.reference.Ref;

@RunWith(Parameterized.class)
public class ArgumentsTest {

    final private static String VALID_NAME = "name";
    final private static ValueExpression VALID_VE = con(1);
    final private static Reducer VALID_REDUCER = new Reducer() { @Override public ValueExpression reduce(final ValueExpression l, final ValueExpression r) { return null; }};
    final private static Expression VALID_E = new Expression() { @Override public boolean eval(final Environment env, final Encoding enc) { return false; }};

    private final Class<?> _class;
    private final Object[] _arguments;

    @Parameters(name = "{0}")
    public static Collection<Object[]> arguments() {
        return Arrays.asList(new Object[][] {
            // Derived directly from ValueExpression
            { FoldLeft.class, new Object[] { VALID_NAME, null, VALID_VE } },
            { FoldLeft.class, new Object[] { null, VALID_REDUCER, VALID_VE } },
            { FoldRight.class, new Object[] { VALID_NAME, null, VALID_VE } },
            { FoldRight.class, new Object[] { null, VALID_REDUCER, VALID_VE } },
            { First.class, new Object[] { null } },
            { Offset.class, new Object[] { null } },
            { Ref.class, new Object[] { null } },
            // Derived from BinaryValueExpression
            { Cat.class, new Object[] { VALID_VE, null } },
            { Cat.class, new Object[] { null, VALID_VE } },
            // Derived from UnaryValueExpression
            { Neg.class, new Object[] { null } },
            // Derived from BinaryLogicalExpression
            { And.class, new Object[] { VALID_E, null } },
            { And.class, new Object[] { null, VALID_E } },
            // Derived from UnaryLogicalExpression
            { Not.class, new Object[] { null } },
            // Derived from ComparisonExpression
            { Eq.class, new Object[] { VALID_VE, null } },
        });
    }

    public ArgumentsTest(final Class<?> theClass, final Object[] arguments) {
        _class = theClass;
        _arguments = arguments;
    }

    @Test(expected=IllegalArgumentException.class)
    public void runConstructor() throws Throwable {
        final Constructor<?>[] constructors = _class.getConstructors();
        Assert.assertEquals(1, constructors.length);
        try {
            constructors[0].newInstance(_arguments);
        }
        catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
