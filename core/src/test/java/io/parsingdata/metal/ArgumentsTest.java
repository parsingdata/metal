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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.logical.And;
import io.parsingdata.metal.expression.logical.Not;
import io.parsingdata.metal.expression.value.*;
import io.parsingdata.metal.expression.value.arithmetic.Neg;
import io.parsingdata.metal.expression.value.reference.*;
import io.parsingdata.metal.token.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static io.parsingdata.metal.Shorthand.con;

@RunWith(Parameterized.class)
public class ArgumentsTest {

    final private static String VALID_NAME = "name";
    final private static ValueExpression VALID_VE = con(1);
    final private static Reducer VALID_REDUCER = new Reducer() { @Override public ValueExpression reduce(final ValueExpression left, final ValueExpression right) { return null; }};
    final private static Expression VALID_E = new Expression() { @Override public boolean eval(final Environment env, final Encoding enc) { return false; }};
    final private static Token VALID_T = new Token("", null) { @Override protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException { return null; } };

    private final Class<?> _class;
    private final Object[] _arguments;

    @Parameters(name = "{index}-{0}")
    public static Collection<Object[]> arguments() {
        return Arrays.asList(new Object[][] {
            // Derived directly from ValueExpression
            { FoldLeft.class, new Object[] { VALID_VE, null, VALID_VE } },
            { FoldLeft.class, new Object[] { null, VALID_REDUCER, VALID_VE } },
            { FoldRight.class, new Object[] { VALID_VE, null, VALID_VE } },
            { FoldRight.class, new Object[] { null, VALID_REDUCER, VALID_VE } },
            { First.class, new Object[] { null } },
            { Offset.class, new Object[] { null } },
            { NameRef.class, new Object[] { null } },
            { TokenRef.class, new Object[] { null } },
            { Count.class, new Object[] { null } },
            // Derived from BinaryValueExpression
            { Cat.class, new Object[] { VALID_VE, null } },
            { Cat.class, new Object[] { null, VALID_VE } },
            // Derived from UnaryValueExpression
            { Neg.class, new Object[] { null } },
            { Len.class, new Object[] { null } },
            // Derived from BinaryLogicalExpression
            { And.class, new Object[] { VALID_E, null } },
            { And.class, new Object[] { null, VALID_E } },
            // Derived from UnaryLogicalExpression
            { Not.class, new Object[] { null } },
            // Derived from ComparisonExpression
            { Eq.class, new Object[] { VALID_VE, null } },
            // Token implementations
            { Cho.class, new Object[] { null, null, new Token[] { VALID_T, VALID_T } } },
            { Cho.class, new Object[] { VALID_NAME, null, new Token[] { VALID_T, null } } },
            { Cho.class, new Object[] { VALID_NAME, null, new Token[] { null, VALID_T } } },
            { Cho.class, new Object[] { VALID_NAME, null, null } },
            { Def.class, new Object[] { VALID_NAME, null, null, null } },
            { Def.class, new Object[] { null, VALID_VE, null, null } },
            { Nod.class, new Object[] { null, VALID_VE, null } },
            { Nod.class, new Object[] { VALID_NAME, null, null } },
            { Opt.class, new Object[] { null, VALID_T, null } },
            { Opt.class, new Object[] { VALID_NAME, null, null } },
            { Pre.class, new Object[] { null, VALID_T, null, null } },
            { Pre.class, new Object[] { VALID_NAME, null, null, null } },
            { Rep.class, new Object[] { null, VALID_T, null } },
            { Rep.class, new Object[] { VALID_NAME, null, null } },
            { RepN.class, new Object[] { null, VALID_T, VALID_VE, null } },
            { RepN.class, new Object[] { VALID_NAME, null, VALID_VE, null } },
            { RepN.class, new Object[] { VALID_NAME, VALID_T, null, null } },
            { Seq.class, new Object[] { null, null, new Token[] { VALID_T, VALID_T } } },
            { Seq.class, new Object[] { VALID_NAME, null, new Token[] { VALID_T, null } } },
            { Seq.class, new Object[] { VALID_NAME, null, new Token[] { null, VALID_T } } },
            { Seq.class, new Object[] { VALID_NAME, null, null } },
            { Str.class, new Object[] { VALID_NAME, null, null, null, null } },
            { Str.class, new Object[] { null, VALID_T, null, null, null } },
            { Sub.class, new Object[] { null, VALID_T, VALID_VE, null } },
            { Sub.class, new Object[] { VALID_NAME, VALID_T, null, null } },
            { Sub.class, new Object[] { VALID_NAME, null, VALID_VE, null } },
            { While.class, new Object[] { null, VALID_T, null, null } },
            { While.class, new Object[] { VALID_NAME, null, null, null } }
        });
    }

    public ArgumentsTest(final Class<?> theClass, final Object[] arguments) {
        _class = theClass;
        _arguments = arguments;
    }

    @Test
    public void runConstructor() throws Throwable {
        final Constructor<?>[] constructors = _class.getConstructors();
        Assert.assertEquals(1, constructors.length);
        try {
            constructors[0].newInstance(_arguments);
            Assert.fail("Should have thrown an IllegalArgumentException.");
        }
        catch (final InvocationTargetException e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            Assert.assertTrue(e.getCause().getMessage().endsWith("may not be null."));
        }
    }

}
