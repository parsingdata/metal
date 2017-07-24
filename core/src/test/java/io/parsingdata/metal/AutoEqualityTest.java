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
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Util.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.junit.runners.Parameterized.Parameter;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ByteStreamSource;
import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.DataExpressionSource;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.value.Bytes;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.Const;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.Elvis;
import io.parsingdata.metal.expression.value.Expand;
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
import io.parsingdata.metal.expression.value.reference.Nth;
import io.parsingdata.metal.expression.value.reference.Offset;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Nod;
import io.parsingdata.metal.token.Post;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.Tie;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.Until;
import io.parsingdata.metal.token.While;
import io.parsingdata.metal.util.InMemoryByteStream;

@RunWith(Parameterized.class)
public class AutoEqualityTest {

    private static final ParseValue PARSEVALUE = new ParseValue("a", any("a"), createFromBytes(new byte[]{1, 2}), enc());
    private static final ParseGraph GRAPH_WITH_REFERENCE = ParseGraph.EMPTY.add(new ParseReference(0, new ConstantSource(new byte[]{1, 2}), any("a")));
    private static final ParseGraph BRANCHED_GRAPH = new Environment((offset, data) -> 0).addBranch(any("a")).order;
    private static final ParseGraph CLOSED_BRANCHED_GRAPH = new Environment((offset, data) -> 0).addBranch(any("a")).closeBranch().order;

    private static final List<Object> STRINGS = Arrays.asList("a", "b");
    private static final List<Object> ENCODINGS = Arrays.asList(enc(), signed(), le(), new Encoding(Charset.forName("UTF-8")));
    private static final List<Object> TOKENS = Arrays.asList(any("a"), any("b"));
    private static final List<Object> TOKEN_ARRAYS = Arrays.asList(new Token[] { any("a"), any("b")}, new Token[] { any("b"), any("c") }, new Token[] { any("a"), any("b"), any("c") });
    private static final List<Object> VALUE_EXPRESSIONS = Arrays.asList(con(1), con(2));
    private static final List<Object> EXPRESSIONS = Arrays.asList(expTrue(), not(expTrue()));
    private static final List<Object> VALUES = Arrays.asList(ConstantFactory.createFromString("a", enc()), ConstantFactory.createFromString("b", enc()), ConstantFactory.createFromNumeric(1L, signed()));
    private static final List<Object> REDUCERS = Arrays.asList((BinaryOperator<ValueExpression>) Shorthand::cat, (BinaryOperator<ValueExpression>) Shorthand::div);
    private static final List<Object> SLICES = Arrays.asList(createFromBytes(new byte[] { 1, 2 }), new Slice(new DataExpressionSource(ref("a"), 1, ParseGraph.EMPTY, enc()), 0, new byte[] { 0, 0 }));
    private static final List<Object> BYTE_ARRAYS = Arrays.asList(new byte[] { 0 }, new byte[] { 1, 2 }, new byte[] {});
    private static final List<Object> SOURCES = Arrays.asList(new ConstantSource(new byte[] {}), new DataExpressionSource(ref("x"), 8, ParseGraph.EMPTY.add(PARSEVALUE), signed()));
    private static final List<Object> LONGS = Arrays.asList(0L, 1L, 31L, 100000L);
    private static final List<Object> INTEGERS = Arrays.asList(0, 1, 17, 21212121);
    private static final List<Object> PARSEGRAPHS = Arrays.asList(ParseGraph.EMPTY, GRAPH_WITH_REFERENCE);
    private static final List<Object> PARSEITEMS = Arrays.asList(CLOSED_BRANCHED_GRAPH, ParseGraph.EMPTY, GRAPH_WITH_REFERENCE, ParseGraph.EMPTY.add(PARSEVALUE), ParseGraph.EMPTY.add(PARSEVALUE).add(PARSEVALUE), BRANCHED_GRAPH);
    private static final List<Object> BYTESTREAMS = Arrays.asList(new InMemoryByteStream(new byte[] { 1, 2 }), (ByteStream) (offset, data) -> 0);
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
        put(byte[].class, BYTE_ARRAYS);
        put(Source.class, SOURCES);
        put(long.class, LONGS);
        put(int.class, INTEGERS);
        put(ParseGraph.class, PARSEGRAPHS);
        put(ParseItem.class, PARSEITEMS);
        put(ByteStream.class, BYTESTREAMS);
    }};

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return generateObjectArrays(
            // Tokens
            Cho.class, Def.class, Nod.class, Pre.class, Rep.class, RepN.class, Seq.class, Sub.class, Tie.class,
            TokenRef.class, While.class, Post.class, Until.class,
            // ValueExpressions
            Len.class, Offset.class, Neg.class, Not.class, Count.class, First.class, Last.class, Reverse.class,
            And.class, Or.class, ShiftLeft.class, ShiftRight.class, Add.class, Div.class, Mod.class, Mul.class,
            io.parsingdata.metal.expression.value.arithmetic.Sub.class, Cat.class, Nth.class, Elvis.class,
            FoldLeft.class, FoldRight.class, Const.class, Expand.class, Bytes.class,
            // Expressions
            Eq.class, EqNum.class, EqStr.class, GtNum.class, LtNum.class,
            io.parsingdata.metal.expression.logical.And.class, io.parsingdata.metal.expression.logical.Or.class,
            io.parsingdata.metal.expression.logical.Not.class,
            // Data structures
            Value.class, ParseValue.class, ParseReference.class,
            // Inputs
            ConstantSource.class, DataExpressionSource.class, ByteStreamSource.class, Slice.class
            );
    }

    private static Collection<Object[]> generateObjectArrays(Class... classes) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Collection<Object[]> results = new ArrayList<>();
        for (Class c : classes) {
            results.add(generateObjectArrays(c));
        }
        return results;
    }

    private static Object[] generateObjectArrays(Class c) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor cons = c.getConstructors()[0];
        List<List<Object>> args = new ArrayList<>();
        for (Class cl : cons.getParameterTypes()) {
            args.add(mapping.get(cl));
        }
        List<List<Object>> argLists = generateCombinations(0, args);
        List<Object> otherInstances = new ArrayList<>();
        for (List<Object> argList : argLists.subList(1, argLists.size())) {
            otherInstances.add(cons.newInstance(argList.toArray()));
        }
        return new Object[]{
            cons.newInstance(argLists.get(0).toArray()),
            cons.newInstance(argLists.get(0).toArray()),
            otherInstances.toArray()
        };
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

    @Parameter(0) public Object object;
    @Parameter(1) public Object same;
    @Parameter(2) public Object[] other;

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
