/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.data.ByteStreamSourceTest.DUMMY_BYTE_STREAM_SOURCE;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ByteStreamSource;
import io.parsingdata.metal.data.ConcatenatedValueSource;
import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.DataExpressionSource;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtEqNum;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtEqNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.value.Bytes;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.Const;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Elvis;
import io.parsingdata.metal.expression.value.Expand;
import io.parsingdata.metal.expression.value.FoldCat;
import io.parsingdata.metal.expression.value.FoldLeft;
import io.parsingdata.metal.expression.value.FoldRight;
import io.parsingdata.metal.expression.value.Reverse;
import io.parsingdata.metal.expression.value.Scope;
import io.parsingdata.metal.expression.value.SingleValueExpression;
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
import io.parsingdata.metal.expression.value.reference.CurrentIteration;
import io.parsingdata.metal.expression.value.reference.CurrentOffset;
import io.parsingdata.metal.expression.value.reference.First;
import io.parsingdata.metal.expression.value.reference.Last;
import io.parsingdata.metal.expression.value.reference.Len;
import io.parsingdata.metal.expression.value.reference.Nth;
import io.parsingdata.metal.expression.value.reference.Offset;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Post;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.Tie;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.DefUntil;
import io.parsingdata.metal.token.While;
import io.parsingdata.metal.util.EncodingFactory;
import io.parsingdata.metal.util.InMemoryByteStream;

@SuppressWarnings("PMD.EqualsNull") // Suppressed because this class explicitly checks for correct equals(null) behaviour.
@RunWith(Parameterized.class)
public class AutoEqualityTest {

    @Parameter public Object object;
    @Parameter(1) public Object same;
    @Parameter(2) public Object[] other;

    public static final Object OTHER_TYPE = new Object() {};

    public static final ByteStream DUMMY_STREAM = new ByteStream() {
        @Override public byte[] read(BigInteger offset, int length) { return new byte[0]; }
        @Override public boolean isAvailable(BigInteger offset, BigInteger length) { return false; }
    };

    private static final ParseValue PARSE_VALUE = new ParseValue("a", any("a"), createFromBytes(new byte[]{1, 2}), enc());
    private static final ParseGraph GRAPH_WITH_REFERENCE = createFromByteStream(DUMMY_STREAM).createCycle(new ParseReference(ZERO, new ConstantSource(new byte[]{1, 2}), any("a"))).order;
    private static final ParseGraph BRANCHED_GRAPH = createFromByteStream(DUMMY_STREAM).addBranch(any("a")).order;
    private static final ParseGraph CLOSED_BRANCHED_GRAPH = createFromByteStream(DUMMY_STREAM).addBranch(any("a")).closeBranch(any("a")).order;

    private static final List<Supplier<Object>> STRINGS = Arrays.asList(() -> "a", () -> "b");
    private static final List<Supplier<Object>> ENCODINGS = Arrays.asList(EncodingFactory::enc, EncodingFactory::signed, EncodingFactory::le, () -> new Encoding(Charset.forName("UTF-8")));
    private static final List<Supplier<Object>> TOKENS = Arrays.asList(() -> any("a"), () -> any("b"));
    private static final List<Supplier<Object>> TOKEN_ARRAYS = Arrays.asList(() -> new Token[] { any("a"), any("b")}, () -> new Token[] { any("b"), any("c") }, () -> new Token[] { any("a"), any("b"), any("c") });
    private static final List<Supplier<Object>> SINGLE_VALUE_EXPRESSIONS = Arrays.asList(() -> con(1), () -> con(2));
    private static final List<Supplier<Object>> VALUE_EXPRESSIONS = Arrays.asList(() -> con(1), () -> exp(con(1), con(2)));
    private static final List<Supplier<Object>> EXPRESSIONS = Arrays.asList(() -> TRUE, () -> not(TRUE));
    private static final List<Supplier<Object>> VALUES = Arrays.asList(() -> ConstantFactory.createFromString("a", enc()), () -> ConstantFactory.createFromString("b", enc()), () -> ConstantFactory.createFromNumeric(1L, signed()), () -> NOT_A_VALUE);
    private static final List<Supplier<Object>> REDUCERS = Arrays.asList(() -> (BinaryOperator<ValueExpression>) Shorthand::cat, () -> (BinaryOperator<ValueExpression>) Shorthand::div);
    private static final List<Supplier<Object>> SLICES = Arrays.asList(() -> createFromBytes(new byte[] { 1, 2 }), () -> Slice.createFromSource(new DataExpressionSource(ref("a"), 1, createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE).add(PARSE_VALUE), enc()), ZERO, BigInteger.valueOf(2)).get());
    private static final List<Supplier<Object>> BYTE_ARRAYS = Arrays.asList(() -> new byte[] { 0 }, () -> new byte[] { 1, 2 }, () -> new byte[] {});
    private static final List<Supplier<Object>> SOURCES = Arrays.asList(() -> new ConstantSource(new byte[] {}), () -> new DataExpressionSource(ref("x"), 8, createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE), signed()));
    private static final List<Supplier<Object>> LONGS = Arrays.asList(() -> 0L, () -> 1L, () -> 31L, () -> 100000L);
    private static final List<Supplier<Object>> INTEGERS = Arrays.asList(() -> 0, () -> 1, () -> 17, () -> 21212121);
    private static final List<Supplier<Object>> PARSE_GRAPHS = Arrays.asList(() -> ParseGraph.EMPTY, () -> GRAPH_WITH_REFERENCE);
    private static final List<Supplier<Object>> PARSE_ITEMS = Arrays.asList(() -> CLOSED_BRANCHED_GRAPH, () -> ParseGraph.EMPTY, () -> GRAPH_WITH_REFERENCE, () -> createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE).order, () -> createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE).add(PARSE_VALUE).order, () -> BRANCHED_GRAPH);
    private static final List<Supplier<Object>> BYTE_STREAMS = Arrays.asList(() -> new InMemoryByteStream(new byte[] { 1, 2 }), () -> DUMMY_STREAM);
    private static final List<Supplier<Object>> BIG_INTEGERS = Arrays.asList(() -> ONE, () -> BigInteger.valueOf(3));
    private static final List<Supplier<Object>> PARSE_STATES = Arrays.asList(() -> createFromByteStream(DUMMY_STREAM), () -> createFromByteStream(DUMMY_STREAM, ONE), () -> new ParseState(GRAPH_WITH_REFERENCE, DUMMY_BYTE_STREAM_SOURCE, TEN, new ImmutableList<>(), new ImmutableList<>()));
    private static final List<Supplier<Object>> IMMUTABLE_LISTS = Arrays.asList(ImmutableList::new, () -> ImmutableList.create("TEST"), () -> ImmutableList.create(1), () -> ImmutableList.create(1).add(2));
    private static final List<Supplier<Object>> BOOLEANS = Arrays.asList(() -> true, () -> false);
    private static final Map<Class, List<Supplier<Object>>> mapping = buildMap();

    private static Map<Class, List<Supplier<Object>>> buildMap() {
        final Map<Class, List<Supplier<Object>>> result = new HashMap<>();
        result.put(String.class, STRINGS);
        result.put(Encoding.class, ENCODINGS);
        result.put(Token.class, TOKENS);
        result.put(Token[].class, TOKEN_ARRAYS);
        result.put(SingleValueExpression.class, SINGLE_VALUE_EXPRESSIONS);
        result.put(ValueExpression.class, VALUE_EXPRESSIONS);
        result.put(Expression.class, EXPRESSIONS);
        result.put(Value.class, VALUES);
        result.put(BinaryOperator.class, REDUCERS);
        result.put(Slice.class, SLICES);
        result.put(byte[].class, BYTE_ARRAYS);
        result.put(Source.class, SOURCES);
        result.put(long.class, LONGS);
        result.put(int.class, INTEGERS);
        result.put(ParseGraph.class, PARSE_GRAPHS);
        result.put(ParseItem.class, PARSE_ITEMS);
        result.put(ByteStream.class, BYTE_STREAMS);
        result.put(BigInteger.class, BIG_INTEGERS);
        result.put(ParseState.class, PARSE_STATES);
        result.put(ImmutableList.class, IMMUTABLE_LISTS);
        result.put(boolean.class, BOOLEANS);
        return result;
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return generateObjectArrays(
            // Tokens
            Cho.class, Def.class, Pre.class, Rep.class, RepN.class, Seq.class, Sub.class, Tie.class,
            TokenRef.class, While.class, Post.class, DefUntil.class,
            // ValueExpressions
            Len.class, Offset.class, Neg.class, Not.class, Count.class, First.class, Last.class, Reverse.class,
            And.class, Or.class, ShiftLeft.class, ShiftRight.class, Add.class, Div.class, Mod.class, Mul.class,
            io.parsingdata.metal.expression.value.arithmetic.Sub.class, Cat.class, Nth.class, Elvis.class,
            FoldLeft.class, FoldRight.class, Const.class, Expand.class, Bytes.class, CurrentOffset.class,
            FoldCat.class, CurrentIteration.class, Scope.class,
            // Expressions
            Eq.class, EqNum.class, EqStr.class, GtEqNum.class, GtNum.class, LtEqNum.class, LtNum.class,
            io.parsingdata.metal.expression.logical.And.class, io.parsingdata.metal.expression.logical.Or.class,
            io.parsingdata.metal.expression.logical.Not.class,
            // Data structures
            CoreValue.class, ParseValue.class, ParseReference.class, ParseState.class,
            // Inputs
            ConstantSource.class, DataExpressionSource.class, ByteStreamSource.class, ConcatenatedValueSource.class
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
        Constructor cons = c.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        List<List<Supplier<Object>>> args = new ArrayList<>();
        for (Class cl : cons.getParameterTypes()) {
            args.add(mapping.get(cl));
        }
        List<List<Supplier<Object>>> argLists = generateCombinations(0, args);
        List<Object> otherInstances = new ArrayList<>();
        for (List<Supplier<Object>> argList : argLists.subList(1, argLists.size())) {
            otherInstances.add(cons.newInstance(instantiate(argList).toArray()));
        }
        return new Object[]{
            cons.newInstance(instantiate(argLists.get(0)).toArray()),
            cons.newInstance(instantiate(argLists.get(0)).toArray()),
            otherInstances.toArray()
        };
    }

    private static List<List<Supplier<Object>>> generateCombinations(int index, List<List<Supplier<Object>>> args) {
        List<List<Supplier<Object>>> result = new ArrayList<>();
        if (index == args.size()) {
            result.add(new ArrayList<>());
        } else {
            for (Supplier<Object> obj : args.get(index)) {
                for (List<Supplier<Object>> list : generateCombinations(index + 1, args)) {
                    list.add(0, obj);
                    result.add(list);
                }
            }
        }
        return result;
    }

    private static List<Object> instantiate(final List<Supplier<Object>> suppliers) {
        List<Object> output = new ArrayList<>();
        for (Supplier<Object> supplier : suppliers) {
            output.add(supplier.get());
        }
        return output;
    }

    @Test
    public void notEqualsNull() {
        assertFalse(object.equals(null));
        assertFalse(other.equals(null));
        for (Object o : other) {
            assertFalse(o.equals(null));
        }
    }

    @Test
    public void equalsItselfIdentity() {
        assertEquals(object, object);
        assertEquals(same, same);
        for (Object o : other) {
            assertEquals(o, o);
        }
    }

    @Test
    public void equalsItself() {
        assertEquals(object, same);
        assertEquals(same, object);
    }

    @Test
    public void notEquals() {
        for (Object o : other) {
            assertNotEquals(o, object);
            assertNotEquals(object, o);
            assertNotEquals(o, same);
            assertNotEquals(same, o);
        }
    }

    @Test
    public void notEqualsType() {
        assertNotEquals(object, OTHER_TYPE);
        assertNotEquals(OTHER_TYPE, object);
        assertNotEquals(other, OTHER_TYPE);
        assertNotEquals(OTHER_TYPE, other);
        for (Object o : other) {
            assertNotEquals(o, OTHER_TYPE);
            assertNotEquals(OTHER_TYPE, o);
        }
    }

    @Test
    public void basicNoHashCollisions() {
        assertEquals(object.hashCode(), object.hashCode());
        assertEquals(same.hashCode(), same.hashCode());
        assertEquals(object.hashCode(), same.hashCode());
        assertNotEquals(object.hashCode(), OTHER_TYPE.hashCode());
        assertNotEquals(same.hashCode(), OTHER_TYPE.hashCode());
        for (Object o : other) {
            assertEquals(o.hashCode(), o.hashCode());
            assertNotEquals(o.hashCode(), OTHER_TYPE.hashCode());
        }
    }

}
