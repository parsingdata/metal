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

package io.parsingdata.metal.util.serialize.constraint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.Util;
import io.parsingdata.metal.util.stream.ArrayByteStream;

public class TransformConstraintTest {

    private static final Token INNER = str("INNER", def("value", 1));
    private static final Token OUTER = str("OUTER", repn(INNER, con(3)));
    private static final Token OUTER2 = str("OUTER2", repn(INNER, con(3)));

    // These names are swapped on purpose, INNER2 = outer token.
    private static final Token OUTER3 = str("OUTER", def("value", 1));
    private static final Token INNER2 = str("INNER", repn(OUTER3, con(3)));

    private static final Token SUB =
        seq(
            def("ptr1", 1),
            sub(OUTER, ref("ptr1")));

    private static final Token DOUBLE_SUB_INNER2 = str("INNER2", def("value", 1));

    private static final Token DOUBLE_SUB_INNER1 =
        str("INNER1",
            seq(
                def("ptr2", 1),
                sub(DOUBLE_SUB_INNER2, ref("ptr2"))));

    private static final Token DOUBLE_SUB =
        str("OUTER",
            seq(
                def("ptr1", 1),
                sub(DOUBLE_SUB_INNER1, ref("ptr1"))));

    private static final Token THREE_VALUES = seq(def("value1", 1), def("value2", 1), def("value3", 1));

    @Test
    public void testValid() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1, 2, 3}, OUTER);

        final TransformConstraint transformConstraint = new TransformConstraint(OUTER, INNER);

        assertTrue(result.succeeded());
        assertTrue(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void missingScope() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1, 2, 3}, OUTER2);

        final TransformConstraint transformConstraint = new TransformConstraint(OUTER, INNER);

        assertTrue(result.succeeded());
        assertFalse(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void swappedScopes() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1, 2, 3}, INNER2);

        final TransformConstraint transformConstraint = new TransformConstraint(OUTER, INNER);

        assertTrue(result.succeeded());
        assertFalse(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void subToOuter() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1, 1, 2, 3}, SUB);

        final TransformConstraint transformConstraint = new TransformConstraint(OUTER, INNER);

        assertTrue(result.succeeded());
        assertTrue(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void doubleSubToOuter() throws IOException {
        final ParseResult result = Util.parse(new byte[]{2, 42, 1}, DOUBLE_SUB);

        final TransformConstraint transformConstraint = new TransformConstraint(DOUBLE_SUB, DOUBLE_SUB_INNER1, DOUBLE_SUB_INNER2);

        assertTrue(result.succeeded());
        assertTrue(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void doubleSubToOuterWithoutIntermediate() throws IOException {
        final ParseResult result = Util.parse(new byte[]{2, 42, 1}, DOUBLE_SUB);

        final TransformConstraint transformConstraint = new TransformConstraint(DOUBLE_SUB, DOUBLE_SUB_INNER2);

        assertTrue(result.succeeded());
        assertTrue(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void emptyEnvironmentShouldNotSatisfy() throws IOException {
        final TransformConstraint transformConstraint = new TransformConstraint(OUTER, INNER);
        assertFalse(transformConstraint.isSatisfiedBy(new Environment(new ArrayByteStream(new byte[0]))));
    }

    @Test
    public void testValidExpression() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1}, INNER);

        final TransformConstraint transformConstraint = new TransformConstraint(eqNum(ref("value"), con(1)));

        assertTrue(result.succeeded());
        assertTrue(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void testInvalidExpression() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1}, INNER);

        final TransformConstraint transformConstraint = new TransformConstraint(eqNum(ref("value"), con(2)), INNER);

        assertTrue(result.succeeded());
        assertFalse(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void doubleSubToOuterWithoutIntermediateWithInvalidExpression() throws IOException {
        final ParseResult result = Util.parse(new byte[]{2, 42, 1}, DOUBLE_SUB);

        final TransformConstraint transformConstraint = new TransformConstraint(eqNum(ref("value"), con(43)), DOUBLE_SUB, DOUBLE_SUB_INNER2);

        assertTrue(result.succeeded());
        assertFalse(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void testAndExpression() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1, 2, 3}, THREE_VALUES);

        final TransformConstraint transformConstraint = new TransformConstraint(and(eqNum(ref("value1"), con(1)), eqNum(ref("value3"), con(3))));

        assertTrue(result.succeeded());
        assertTrue(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }

    @Test
    public void testExpressionNonExistingValue() throws IOException {
        final ParseResult result = Util.parse(new byte[]{1, 2, 3}, THREE_VALUES);

        final TransformConstraint transformConstraint = new TransformConstraint(eqNum(ref("value0"), con(1)));

        assertTrue(result.succeeded());
        assertFalse(transformConstraint.isSatisfiedBy(result.getEnvironment()));
    }
}