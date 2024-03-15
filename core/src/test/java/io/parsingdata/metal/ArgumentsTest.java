/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import static io.parsingdata.metal.Shorthand.con;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtEqNum;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtEqNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.logical.And;
import io.parsingdata.metal.expression.logical.Not;
import io.parsingdata.metal.expression.value.Bytes;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.FoldLeft;
import io.parsingdata.metal.expression.value.FoldRight;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.arithmetic.Neg;
import io.parsingdata.metal.expression.value.reference.Count;
import io.parsingdata.metal.expression.value.reference.First;
import io.parsingdata.metal.expression.value.reference.Last;
import io.parsingdata.metal.expression.value.reference.Len;
import io.parsingdata.metal.expression.value.reference.Nth;
import io.parsingdata.metal.expression.value.reference.Offset;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.DefUntil;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.While;

public class ArgumentsTest {

    final private static String VALID_NAME = "name";
    final private static String EMPTY_NAME = "";
    final private static ValueExpression VALID_VE = con(1);
    final private static BinaryOperator<ValueExpression> VALID_REDUCER = (left, right) -> null;
    final private static Expression VALID_E = (parseState, encoding) -> false;
    final private static Token VALID_T = new Token("", null) {
        @Override protected Optional<ParseState> parseImpl(final Environment environment) {
            return Optional.empty();
        }
    };

    public static Collection<Object[]> arguments() {
        return List.of(new Object[][] {
            // Derived directly from ValueExpression
            { FoldLeft.class, new Object[] { VALID_VE, null, VALID_VE } },
            { FoldLeft.class, new Object[] { null, VALID_REDUCER, VALID_VE } },
            { FoldRight.class, new Object[] { VALID_VE, null, VALID_VE } },
            { FoldRight.class, new Object[] { null, VALID_REDUCER, VALID_VE } },
            { First.class, new Object[] { null } },
            { Last.class, new Object[] { null } },
            { Nth.class, new Object[] { VALID_VE, null } },
            { Nth.class, new Object[] { null, VALID_VE } },
            { Offset.class, new Object[] { null } },
            { Count.class, new Object[] { null } },
            { Bytes.class, new Object[] { null } },
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
            { EqNum.class, new Object[] { VALID_VE, null } },
            { EqStr.class, new Object[] { VALID_VE, null } },
            { GtEqNum.class, new Object[] { VALID_VE, null } },
            { GtNum.class, new Object[] { VALID_VE, null } },
            { LtEqNum.class, new Object[] { VALID_VE, null } },
            { LtNum.class, new Object[] { VALID_VE, null } },
            // Token implementations
            { Cho.class, new Object[] { null, null, VALID_T, VALID_T, new Token[] { VALID_T } } },
            { Cho.class, new Object[] { VALID_NAME, null, null, VALID_T, new Token[] { VALID_T } } },
            { Cho.class, new Object[] { VALID_NAME, null, VALID_T, null, new Token[] { VALID_T } } },
            { Cho.class, new Object[] { VALID_NAME, null, VALID_T, VALID_T, new Token[] { null } } },
            { Cho.class, new Object[] { VALID_NAME, null, VALID_T, VALID_T, null } },
            { Def.class, new Object[] { VALID_NAME, null, null } },
            { Def.class, new Object[] { null, VALID_VE, null } },
            { Pre.class, new Object[] { null, VALID_T, null, null } },
            { Pre.class, new Object[] { VALID_NAME, null, null, null } },
            { Rep.class, new Object[] { null, VALID_T, null } },
            { Rep.class, new Object[] { VALID_NAME, null, null } },
            { RepN.class, new Object[] { null, VALID_T, VALID_VE, null } },
            { RepN.class, new Object[] { VALID_NAME, null, VALID_VE, null } },
            { RepN.class, new Object[] { VALID_NAME, VALID_T, null, null } },
            { Seq.class, new Object[] { null, null, VALID_T, VALID_T, new Token[] { VALID_T } } },
            { Seq.class, new Object[] { VALID_NAME, null, null, VALID_T, new Token[] { VALID_T } } },
            { Seq.class, new Object[] { VALID_NAME, null, VALID_T, null, new Token[] { VALID_T } } },
            { Seq.class, new Object[] { VALID_NAME, null, VALID_T, VALID_T, new Token[] { null } } },
            { Seq.class, new Object[] { VALID_NAME, null, VALID_T, VALID_T, null } },
            { Sub.class, new Object[] { null, VALID_T, VALID_VE, null } },
            { Sub.class, new Object[] { VALID_NAME, VALID_T, null, null } },
            { Sub.class, new Object[] { VALID_NAME, null, VALID_VE, null } },
            { While.class, new Object[] { null, VALID_T, null, null } },
            { While.class, new Object[] { VALID_NAME, null, null, null } },
            { TokenRef.class, new Object[] { VALID_NAME, null, null } },
            { TokenRef.class, new Object[] { null, VALID_NAME, null } },
            { TokenRef.class, new Object[] { null, null, null } },
            { TokenRef.class, new Object[] { VALID_NAME, EMPTY_NAME, null } },
            { DefUntil.class, new Object[] { null, VALID_VE, VALID_VE, VALID_VE, VALID_T, null }},
            { DefUntil.class, new Object[] { VALID_NAME, VALID_VE, VALID_VE, VALID_VE, null, null }}
        });
    }

    @ParameterizedTest(name = "{index}-{0}")
    @MethodSource("arguments")
    public void runConstructor(final Class<?> clazz, final Object[] arguments) throws Throwable {
        final Constructor<?>[] constructors = clazz.getConstructors();
        assertEquals(1, constructors.length);
        try {
            constructors[0].newInstance(arguments);
            fail("Should have thrown an IllegalArgumentException.");
        }
        catch (final InvocationTargetException e) {
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
            final String message = e.getCause().getMessage();
            assertTrue(message.endsWith("may not be null.") || message.endsWith("may not be empty."));
        }
    }

}
