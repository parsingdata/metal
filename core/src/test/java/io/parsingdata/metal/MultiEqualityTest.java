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

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.logical.Not;
import io.parsingdata.metal.expression.value.Const;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.reference.NameRef;
import io.parsingdata.metal.expression.value.reference.TokenRef;

@RunWith(Parameterized.class)
public class MultiEqualityTest {

    public static final ValueExpression VE = (graph, encoding) -> null;
    public static final String S1 = "string 1";
    public static final String S2 = "string 2";
    public static final Value V1 = ConstantFactory.createFromString(S1, enc());
    public static final Value V2 = ConstantFactory.createFromString(S2, enc());
    public static final byte[] A1 = new byte[] { 1, 2 };
    public static final byte[] A2 = new byte[] { 1, 3 };

    private final Object object;
    private final Object same;
    private final Object other;
    private final Object baseType;

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { new NameRef(S1), new NameRef(S1), new NameRef(S2), VE },
            { new TokenRef(any(S1)), new TokenRef(any(S1)), new TokenRef(any(S2)), VE },
            { new Const(V1), new Const(V1), new Const(V2), VE },
            { new Not(new True()), new Not(new True()), new Not(new Not(new True())), (Expression) (graph, encoding) -> false },
            { new ConstantSource(A1), new ConstantSource(A1), new ConstantSource(A2), new Source() { @Override protected byte[] getData(long offset, int size) throws IOException { return new byte[0]; } } },
        });
    }

    public MultiEqualityTest(final Object object, final Object same, final Object other, final Object baseType) throws NoSuchMethodException {
        this.object = object;
        this.same = same;
        this.other = other;
        this.baseType = baseType;
    }

    @Test
    public void NotEqualsNull() {
        assertFalse(object.equals(null));
        assertFalse(other.equals(null));
    }

    @Test
    public void equalsItselfIdentity() {
        assertTrue(object.equals(object));
        assertTrue(other.equals(other));
    }

    @Test
    public void equalsItself() {
        assertTrue(object.equals(same));
        assertTrue(same.equals(object));
    }

    @Test
    public void notEquals() {
        assertFalse(object.equals(other));
        assertFalse(other.equals(object));
    }

    @Test
    public void notEqualsType() {
        assertFalse(object.equals(baseType));
        assertFalse(baseType.equals(object));
        assertFalse(other.equals(baseType));
        assertFalse(baseType.equals(other));
    }

    @Test
    public void basicNoHashCollisions() {
        assertEquals(object.hashCode(), object.hashCode());
        assertEquals(object.hashCode(), same.hashCode());
        assertNotEquals(object.hashCode(), other.hashCode());
        assertNotEquals(object.hashCode(), baseType.hashCode());
        assertNotEquals(other.hashCode(), baseType.hashCode());
    }

}
