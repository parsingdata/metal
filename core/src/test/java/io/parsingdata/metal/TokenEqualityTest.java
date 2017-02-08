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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Nod;
import io.parsingdata.metal.token.Token;

@RunWith(Parameterized.class)
public class TokenEqualityTest {

    public static final String T1 = "name 1";
    public static final String T2 = "name 2";
    public static final String A1 = "a";
    public static final String A2 = "b";
    public static final String A3 = "c";
    public static final Object BASE_TYPE = new Token(A1, enc()) { @Override protected ParseResult parseImpl(String scope, Environment environment, Encoding encoding) throws IOException { return null; } };

    private final Object object;
    private final Object same;
    private final Object[] other;

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {    /* Cho */
                new Cho(T1, enc(), any(A1), any(A2)), // object
                new Cho(T1, enc(), any(A1), any(A2)), // same
                new Object[] { // other
                    new Cho(T2, enc(), any(A1), any(A2)),
                    new Cho(T1, signed(), any(A1), any(A2)),
                    new Cho(T1, enc(), any(A2), any(A2)),
                    new Cho(T1, enc(), any(A1), any(A3)),
                    new Cho(T1, enc(), any(A1), any(A2), any(A3))
                }
            }, { /* Def */
                new Def(T1, con(1), expTrue(), enc()), // object
                new Def(T1, con(1), expTrue(), enc()), // same
                new Object[] { // other
                    new Def(T2, con(1), expTrue(), enc()),
                    new Def(T1, con(2), expTrue(), enc()),
                    new Def(T1, con(1), not(expTrue()), enc()),
                    new Def(T1, con(1), expTrue(), signed())
                }
            }, { /* Nod */
                new Nod(T1, con(1), enc()), // object
                new Nod(T1, con(1), enc()), // same
                new Object[] { // other
                    new Nod(T2, con(1), enc()),
                    new Nod(T1, con(2), enc()),
                    new Nod(T1, con(1), signed())
                }
            }
        });
    }

    public TokenEqualityTest(final Object object, final Object same, final Object[] other) throws NoSuchMethodException {
        this.object = object;
        this.same = same;
        this.other = other;
    }

    @Test
    public void NotEqualsNull() {
        assertFalse(object.equals(null));
        assertFalse(same.equals(null));
        checkAllFalse(other, (Object) null);
    }

    @Test
    public void equalsItselfIdentity() {
        assertTrue(object.equals(object));
        assertTrue(same.equals(same));
        for (Object o : Arrays.asList(other)) {
            assertTrue(o.equals(o));
        }
    }

    @Test
    public void equalsItself() {
        assertTrue(object.equals(same));
        assertTrue(same.equals(object));
    }

    @Test
    public void notEquals() {
        checkAllFalse(object, other);
        checkAllFalse(other, object);
    }

    @Test
    public void notEqualsType() {
        assertFalse(object.equals(BASE_TYPE));
        assertFalse(BASE_TYPE.equals(object));
        assertFalse(BASE_TYPE.equals(same));
        checkAllFalse(BASE_TYPE, other);
        checkAllFalse(other, BASE_TYPE);
    }

    @Test
    public void noHashCollisions() {
        assertEquals(object.hashCode(), object.hashCode());
        assertEquals(object.hashCode(), same.hashCode());
        for (Object o : Arrays.asList(other)) {
            assertNotEquals(object.hashCode(), o.hashCode());
        }
        assertNotEquals(object.hashCode(), BASE_TYPE.hashCode());
    }

    private void checkAllFalse(final Object object, final Object... targets) {
        checkAllFalse(new Object[] { object }, targets);
    }

    private void checkAllFalse(final Object[] objects, final Object... targets) {
        for (Object o : Arrays.asList(objects)) {
            for (Object t : Arrays.asList(targets)) {
                assertFalse(o.equals(t));
            }
        }
    }

}
