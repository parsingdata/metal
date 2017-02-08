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

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.DataExpressionSource;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.Const;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.Elvis;
import io.parsingdata.metal.expression.value.FoldLeft;
import io.parsingdata.metal.expression.value.FoldRight;
import io.parsingdata.metal.expression.value.Reverse;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.arithmetic.Add;
import io.parsingdata.metal.expression.value.arithmetic.Div;
import io.parsingdata.metal.expression.value.arithmetic.Mod;
import io.parsingdata.metal.expression.value.arithmetic.Mul;
import io.parsingdata.metal.expression.value.arithmetic.Neg;
import io.parsingdata.metal.expression.value.bitwise.And;
import io.parsingdata.metal.expression.value.bitwise.Not;
import io.parsingdata.metal.expression.value.bitwise.Or;
import io.parsingdata.metal.expression.value.bitwise.ShiftLeft;
import io.parsingdata.metal.expression.value.bitwise.ShiftRight;
import io.parsingdata.metal.expression.value.reference.Count;
import io.parsingdata.metal.expression.value.reference.First;
import io.parsingdata.metal.expression.value.reference.Last;
import io.parsingdata.metal.expression.value.reference.Len;
import io.parsingdata.metal.expression.value.reference.NameRef;
import io.parsingdata.metal.expression.value.reference.Nth;
import io.parsingdata.metal.expression.value.reference.Offset;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Nod;
import io.parsingdata.metal.token.Opt;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.Tie;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.While;

@RunWith(Parameterized.class)
public class AutoEqualityTest {

    private static final List<Object> STRINGS = new ArrayList<Object>() {{ add("a"); add("b"); }};
    private static final List<Object> ENCODINGS = new ArrayList<Object>() {{ add(enc()); add(signed()); add(le()); add(new Encoding(Charset.forName("UTF-8"))); }};
    private static final List<Object> TOKENS = new ArrayList<Object>() {{ add(any("a")); add(any("b")); }};
    private static final List<Object> TOKEN_ARRAYS = new ArrayList<Object>() {{ add(new Token[] { any("a"), any("b")}); add(new Token[] { any("b"), any("c") }); add(new Token[] { any("a"), any("b"), any("c") }); }};
    private static final List<Object> VALUE_EXPRESSIONS = new ArrayList<Object>() {{ add(con(1)); add(con(2)); }};
    private static final List<Object> EXPRESSIONS = new ArrayList<Object>() {{ add(expTrue()); add(not(expTrue())); }};
    private static final List<Object> VALUES = new ArrayList<Object>() {{ add(ConstantFactory.createFromString("a", enc())); add(ConstantFactory.createFromString("b", enc())); add(ConstantFactory.createFromNumeric(1L, signed())); }};
    private static final List<Object> REDUCERS = new ArrayList<Object>() {{ add((BinaryOperator<ValueExpression>) (left, right) -> cat(left, right)); add((BinaryOperator<ValueExpression>) (left, right) -> div(left, right)); }};
    private static final List<Object> SLICES = new ArrayList<Object>() {{ add(new Slice(new ConstantSource(new byte[] { 1, 2 }), 0, new byte[] { 1, 2 })); add(new Slice(new DataExpressionSource(ref("a"), 1, ParseGraph.EMPTY, enc()), 0, new byte[] { 0, 0 })); }};
    private static final Map<Class, List<Object>> mapping = new HashMap<Class, List<Object>>() {{
        put(String.class, STRINGS);
        put(Encoding.class, ENCODINGS);
        put(Token.class, TOKENS);
        put(Token[].class, TOKEN_ARRAYS);
        put(ValueExpression.class, VALUE_EXPRESSIONS);
        put(Expression.class, EXPRESSIONS);
        put(Value.class, VALUES);
        put(BinaryOperator.class, REDUCERS);
        put(Slice.class, SLICES);
    }};

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return generateObjectArrays(
            Cho.class, Def.class, Nod.class, Opt.class, Pre.class, Rep.class, RepN.class, Seq.class, Sub.class,
            Tie.class, TokenRef.class, While.class,
            Len.class, Offset.class, Neg.class, Not.class, Count.class, First.class, Last.class, Reverse.class,
            And.class, Or.class, ShiftLeft.class, ShiftRight.class, Add.class, Div.class, Mod.class, Mul.class,
            io.parsingdata.metal.expression.value.arithmetic.Sub.class, Cat.class, Eq.class, EqNum.class, EqStr.class,
            GtNum.class, LtNum.class, Nth.class, Elvis.class, io.parsingdata.metal.expression.logical.And.class,
            io.parsingdata.metal.expression.logical.Or.class, FoldLeft.class, FoldRight.class, Value.class,
            ParseValue.class, io.parsingdata.metal.expression.logical.Not.class, Const.class, NameRef.class,
            io.parsingdata.metal.expression.value.reference.TokenRef.class
        );
    }

    private static Collection<Object[]> generateObjectArrays(Class... classes) {
        Collection<Object[]> results = new ArrayList<>();
        for (Class c : classes) {
            results.add(generateObjectArrays(c));
        }
        return results;
    }

    private static Object[] generateObjectArrays(Class c) {
        Constructor cons = c.getConstructors()[0];
        List<List<Object>> args = new ArrayList<>();
        for (Class cl : cons.getParameterTypes()) {
            args.add(mapping.get(cl));
        }
        List<List<Object>> argLists = generateCombinations(0, args);
        Object[] instances = new Object[3];
        try {
            instances[0] = cons.newInstance(argLists.get(0).toArray());
            instances[1] = cons.newInstance(argLists.get(0).toArray());
            List<Object> otherInstances = new ArrayList<>();
            for (List<Object> argList : argLists.subList(1, argLists.size())) {
                otherInstances.add(cons.newInstance(argList.toArray()));
            }
            instances[2] = otherInstances.toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

    private static List<List<Object>> generateCombinations(int index, List<List<Object>> args) {
        List<List<Object>> result = new ArrayList<>();
        if (index == args.size()) {
            result.add(new ArrayList<>());
        } else {
            for (Object obj : args.get(index)) {
                for (List<Object> list : generateCombinations(index + 1, args)) {
                    list.add(0, obj);
                    result.add(list);
                }
            }
        }
        return result;
    }

    public static Object OTHER_TYPE = new Object() {};

    private final Object object;
    private final Object same;
    private final Object[] other;

    public AutoEqualityTest(final Object object, final Object same, final Object[] other) throws NoSuchMethodException {
        this.object = object;
        this.same = same;
        this.other = other;
    }

    @Test
    public void NotEqualsNull() {
        assertFalse(object.equals(null));
        assertFalse(other.equals(null));
        for (Object o : Arrays.asList(other)) {
            assertFalse(o.equals(null));
        }
    }

    @Test
    public void equalsItselfIdentity() {
        assertTrue(object.equals(object));
        assertTrue(other.equals(other));
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
        for (Object o : Arrays.asList(other)) {
            assertFalse(o.equals(object));
            assertFalse(object.equals(o));
            assertFalse(o.equals(same));
            assertFalse(same.equals(o));
        }
    }

    @Test
    public void notEqualsType() {
        assertFalse(object.equals(OTHER_TYPE));
        assertFalse(OTHER_TYPE.equals(object));
        assertFalse(other.equals(OTHER_TYPE));
        assertFalse(OTHER_TYPE.equals(other));
        for (Object o : Arrays.asList(other)) {
            assertFalse(o.equals(OTHER_TYPE));
            assertFalse(OTHER_TYPE.equals(o));
        }
    }

    @Test
    public void basicNoHashCollisions() {
        assertEquals(object.hashCode(), object.hashCode());
        assertEquals(same.hashCode(), same.hashCode());
        assertEquals(object.hashCode(), same.hashCode());
        assertNotEquals(object.hashCode(), OTHER_TYPE.hashCode());
        assertNotEquals(same.hashCode(), OTHER_TYPE.hashCode());
        for (Object o : Arrays.asList(other)) {
            assertEquals(o.hashCode(), o.hashCode());
            assertNotEquals(o.hashCode(), OTHER_TYPE.hashCode());
        }
    }

}
