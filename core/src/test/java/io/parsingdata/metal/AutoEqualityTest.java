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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.data.ByteStreamSourceTest.DUMMY_BYTE_STREAM_SOURCE;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.ParseValueCache.NO_CACHE;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ByteStreamSource;
import io.parsingdata.metal.data.ConcatenatedValueSource;
import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.DataExpressionSource;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ImmutableList.ReversedImmutableList;
import io.parsingdata.metal.data.ImmutablePair;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueCache;
import io.parsingdata.metal.data.Selection;
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
import io.parsingdata.metal.expression.value.Join;
import io.parsingdata.metal.expression.value.NotAValue;
import io.parsingdata.metal.expression.value.Reverse;
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
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.expression.value.reference.Self;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.DefUntil;
import io.parsingdata.metal.token.Post;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Sub;
import io.parsingdata.metal.token.Tie;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.While;
import io.parsingdata.metal.util.EncodingFactory;
import io.parsingdata.metal.util.InMemoryByteStream;

@SuppressWarnings("PMD.EqualsNull") // Suppressed because this class explicitly checks for correct equals(null) behaviour.
public class AutoEqualityTest {

    private static final Set<Class<?>> CLASSES_TO_TEST = Set.of(
        // Tokens
        Cho.class, Def.class, Pre.class, Rep.class, RepN.class, Seq.class, Sub.class, Tie.class,
        TokenRef.class, While.class, Post.class, DefUntil.class,
        // ValueExpressions
        Len.class, Offset.class, Neg.class, Not.class, Count.class, First.class, Last.class, Reverse.class,
        And.class, Or.class, ShiftLeft.class, ShiftRight.class, Add.class, Div.class, Mod.class, Mul.class,
        io.parsingdata.metal.expression.value.arithmetic.Sub.class, Cat.class, Nth.class, Elvis.class,
        FoldLeft.class, FoldRight.class, Const.class, Expand.class, Bytes.class, CurrentOffset.class,
        FoldCat.class, CurrentIteration.class,
        Join.class, Self.class, Ref.NameRef.class, Ref.DefinitionRef.class,
        // Expressions
        Eq.class, EqNum.class, EqStr.class, GtEqNum.class, GtNum.class, LtEqNum.class, LtNum.class,
        io.parsingdata.metal.expression.logical.And.class, io.parsingdata.metal.expression.logical.Or.class,
        io.parsingdata.metal.expression.logical.Not.class,
        // Data structures
        CoreValue.class, ParseValue.class, ParseReference.class, ParseState.class,
        NotAValue.class, ParseGraph.class, ImmutableList.class, ReversedImmutableList.class,
        ParseValueCache.class,
        // Inputs
        Slice.class,
        ConstantSource.class, DataExpressionSource.class, ByteStreamSource.class, ConcatenatedValueSource.class
    );

    private static final Set<Class<?>> CLASSES_TO_IGNORE = Set.of(
        // Handled indirectly through DefinitionRef and NameRef.
        Ref.class,
        // Handled in EqualityTest manually.
        ImmutablePair.class,
        // Utility classes.
        Selection.class, ConstantFactory.class,
        // Multiple constructors
        Environment.class
    );

    public static final Object OTHER_TYPE = new Object() {};

    public static final ByteStream DUMMY_STREAM = new ByteStream() {
        @Override public byte[] read(BigInteger offset, int length) { return new byte[0]; }
        @Override public boolean isAvailable(BigInteger offset, BigInteger length) { return false; }
    };

    private static final ParseValue PARSE_VALUE = new ParseValue("a", any("a"), createFromBytes(new byte[]{1, 2}), enc());
    private static final ParseGraph GRAPH_WITH_REFERENCE = createFromByteStream(DUMMY_STREAM).createCycle(new ParseReference(ZERO, new ConstantSource(new byte[]{1, 2}), any("a"))).order;
    private static final ParseGraph BRANCHED_GRAPH = createFromByteStream(DUMMY_STREAM).addBranch(any("a")).order;
    private static final ParseGraph CLOSED_BRANCHED_GRAPH = createFromByteStream(DUMMY_STREAM).addBranch(any("a")).closeBranch(any("a")).order;

    private static final List<Supplier<Object>> STRINGS = List.of(() -> "a", () -> "b");
    private static final List<Supplier<Object>> STRING_ARRAYS = List.of(() -> new String[] {"a"}, () -> new String[] {"b"}, () -> new String[] {"a", "b"}, () -> new String[] {"b", "c"}, () -> new String[] {"a", "b", "c"});
    private static final List<Supplier<Object>> ENCODINGS = List.of(EncodingFactory::enc, EncodingFactory::signed, EncodingFactory::le, () -> new Encoding(StandardCharsets.UTF_8));
    private static final List<Supplier<Object>> TOKENS = List.of(() -> any("a"), () -> any("b"));
    private static final List<Supplier<Object>> TOKEN_ARRAYS = List.of(() -> new Token[] { any("a"), any("b")}, () -> new Token[] { any("b"), any("c") }, () -> new Token[] { any("a"), any("b"), any("c") });
    private static final List<Supplier<Object>> SINGLE_VALUE_EXPRESSIONS = List.of(() -> con(1), () -> con(2));
    private static final List<Supplier<Object>> VALUE_EXPRESSIONS = List.of(() -> con(1), () -> exp(con(1), con(2)));
    private static final List<Supplier<Object>> VALUE_EXPRESSION_ARRAY = List.of(() -> new ValueExpression[] { con(1), exp(con(1), con(2))}, () -> new ValueExpression[] { exp(con(1), con(2)), con(1)}, () -> new ValueExpression[] { exp(con(1), con(2)), exp(con(1), con(3))});
    private static final List<Supplier<Object>> EXPRESSIONS = List.of(() -> TRUE, () -> not(TRUE));
    private static final List<Supplier<Object>> VALUES = List.of(() -> ConstantFactory.createFromString("a", enc()), () -> ConstantFactory.createFromString("b", enc()), () -> ConstantFactory.createFromNumeric(1L, signed()), () -> NOT_A_VALUE);
    private static final List<Supplier<Object>> REDUCERS = List.of(() -> (BinaryOperator<ValueExpression>) Shorthand::cat, () -> (BinaryOperator<ValueExpression>) Shorthand::div);
    private static final List<Supplier<Object>> SLICES = List.of(() -> createFromBytes(new byte[] { 1, 2 }), () -> Slice.createFromSource(new DataExpressionSource(ref("a"), 1, createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE).add(PARSE_VALUE), enc()), ZERO, BigInteger.valueOf(2)).get());
    private static final List<Supplier<Object>> BYTE_ARRAYS = List.of(() -> new byte[] { 0 }, () -> new byte[] { 1, 2 }, () -> new byte[] {});
    private static final List<Supplier<Object>> SOURCES = List.of(() -> new ConstantSource(new byte[] {}), () -> new DataExpressionSource(ref("x"), 8, createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE), signed()));
    private static final List<Supplier<Object>> LONGS = List.of(() -> 0L, () -> 1L, () -> 31L, () -> 100000L);
    private static final List<Supplier<Object>> INTEGERS = List.of(() -> 0, () -> 1, () -> 17, () -> 21212121);
    private static final List<Supplier<Object>> PARSE_GRAPHS = List.of(() -> ParseGraph.EMPTY, () -> GRAPH_WITH_REFERENCE);
    private static final List<Supplier<Object>> PARSE_ITEMS = List.of(() -> CLOSED_BRANCHED_GRAPH, () -> ParseGraph.EMPTY, () -> GRAPH_WITH_REFERENCE, () -> createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE).order, () -> createFromByteStream(DUMMY_STREAM).add(PARSE_VALUE).add(PARSE_VALUE).order, () -> BRANCHED_GRAPH);
    private static final List<Supplier<Object>> BYTE_STREAMS = List.of(() -> new InMemoryByteStream(new byte[] { 1, 2 }), () -> DUMMY_STREAM);
    private static final List<Supplier<Object>> BIG_INTEGERS = List.of(() -> ONE, () -> BigInteger.valueOf(3));
    private static final List<Supplier<Object>> PARSE_STATES = List.of(() -> createFromByteStream(DUMMY_STREAM), () -> createFromByteStream(DUMMY_STREAM, ONE), () -> new ParseState(GRAPH_WITH_REFERENCE, NO_CACHE, DUMMY_BYTE_STREAM_SOURCE, TEN, new ImmutableList<>(), new ImmutableList<>(), 0));
    private static final List<Supplier<Object>> PARSE_VALUE_CACHES = List.of(() -> NO_CACHE, ParseValueCache::new, () -> new ParseValueCache().add(PARSE_VALUE), () -> new ParseValueCache().add(PARSE_VALUE).add(PARSE_VALUE));
    private static final List<Supplier<Object>> IMMUTABLE_LISTS = List.of(ImmutableList::new, () -> ImmutableList.create("TEST"), () -> ImmutableList.create(1), () -> ImmutableList.create(1).addHead(2), () -> ImmutableList.create(2).addHead(1).reverse());
    private static final List<Supplier<Object>> LISTS = List.of(List::of, () -> List.of("TEST"), () -> List.of(1), () -> List.of(1,  2), () -> new ImmutableList<>(List.of(1, 2)), () -> new ImmutableList<>(List.of(2, 1)).reverse());
    private static final List<Supplier<Object>> BOOLEANS = List.of(() -> true, () -> false);
    private static final List<Supplier<Object>> BIPREDICATES = List.of(() -> (BiPredicate<Object, Object>) (o, o2) -> false);
    private static final List<Supplier<Object>> MAPS = List.of(Map::of, () -> Map.of("1", 1, "2", 2));
    private static final Map<Class<?>, List<Supplier<Object>>> mapping = buildMap();

    private static Map<Class<?>, List<Supplier<Object>>> buildMap() {
        final Map<Class<?>, List<Supplier<Object>>> result = new HashMap<>();
        result.put(String.class, STRINGS);
        result.put(String[].class, STRING_ARRAYS);
        result.put(Encoding.class, ENCODINGS);
        result.put(Token.class, TOKENS);
        result.put(Token[].class, TOKEN_ARRAYS);
        result.put(SingleValueExpression.class, SINGLE_VALUE_EXPRESSIONS);
        result.put(ValueExpression.class, VALUE_EXPRESSIONS);
        result.put(ValueExpression[].class, VALUE_EXPRESSION_ARRAY);
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
        result.put(ParseValueCache.class, PARSE_VALUE_CACHES);
        result.put(ImmutableList.class, IMMUTABLE_LISTS);
        result.put(boolean.class, BOOLEANS);
        result.put(BiPredicate.class, BIPREDICATES);
        result.put(Map.class, MAPS);
        result.put(List.class, LISTS);
        return result;
    }

    public static Stream<Arguments> data() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Set<Class<?>> classes = findClasses().filter(not(CLASSES_TO_IGNORE::contains)).collect(toSet());
        classes.removeAll(CLASSES_TO_TEST);
        assertEquals(Set.of(), classes, "Please add missing class to the CLASSES_TO_TEST or CLASSES_TO_IGNORE constant.");
        return generateObjectArrays(CLASSES_TO_TEST).stream();
    }

    private static Stream<Class<?>> findClasses() {
        return Stream.of("io.parsingdata.metal.data",
                "io.parsingdata.metal.expression.comparison",
                "io.parsingdata.metal.expression.logical",
                "io.parsingdata.metal.expression.value",
                "io.parsingdata.metal.expression.value.arithmetic",
                "io.parsingdata.metal.expression.value.bitwise",
                "io.parsingdata.metal.expression.value.reference",
                "io.parsingdata.metal.token")
            .flatMap(AutoEqualityTest::findAllClassesUsingClassLoader)
            .filter(c -> Modifier.isPublic(c.getModifiers()))
            .filter(c -> !Modifier.isAbstract(c.getModifiers()));
    }

    public static Stream<Class<?>> findAllClassesUsingClassLoader(final String packageName) {
        try {
            final Iterator<URL> iterator = ClassLoader.getSystemClassLoader()
                .getResources(packageName.replaceAll("[.]", "/")).asIterator();
            return StreamSupport.stream(spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
                .filter(u -> u.getPath().contains("/classes/")) // ignore test classes
                .flatMap(url -> getClasses(packageName, url));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Stream<? extends Class<?>> getClasses(final String packageName, final URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(className -> packageName + "." + className.substring(0, className.lastIndexOf('.')))
                .map(AutoEqualityTest::getClass)
                .collect(toList()).stream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Class<?> getClass(final String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unable to find class for name " + className, e);
        }
    }

    private static Collection<Arguments> generateObjectArrays(final Set<Class<?>> classes) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Collection<Arguments> results = new ArrayList<>();
        for (Class<?> c : classes) {
            results.addAll(generateObjectArrays(c));
        }
        return results;
    }

    private static List<Arguments> generateObjectArrays(final Class<?> c) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final List<Arguments> arguments = new ArrayList<>();
        for (Constructor<?> cons : c.getDeclaredConstructors()) {
            final boolean containsGenericArgument = Arrays.stream(cons.getParameterTypes()).anyMatch(p -> p == Object.class);
            if (containsGenericArgument) {
                break;
            }
            cons.setAccessible(true);
            final List<List<Supplier<Object>>> args = new ArrayList<>();
            for (Class<?> cl : cons.getParameterTypes()) {
                if (!mapping.containsKey(cl)) {
                    throw new AssertionError("Please add a mapping for type " + cl.getSimpleName());
                }
                args.add(mapping.get(cl));
            }
            final List<List<Supplier<Object>>> argLists = generateCombinations(0, args);
            final List<Object> otherInstances = new ArrayList<>();
            for (List<Supplier<Object>> argList : argLists.subList(1, argLists.size())) {
                otherInstances.add(cons.newInstance(instantiate(argList).toArray()));
            }
            arguments.add(Arguments.arguments(
                cons.newInstance(instantiate(argLists.get(0)).toArray()),
                cons.newInstance(instantiate(argLists.get(0)).toArray()),
                otherInstances.toArray()
            ));
        }
        return arguments;
    }

    private static List<List<Supplier<Object>>> generateCombinations(final int index, final List<List<Supplier<Object>>> args) {
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

    @ParameterizedTest
    @MethodSource("data")
    public void notEqualsNull(final Object object, final Object same, final Object[] other) {
        assertFalse(object.equals(null));
        assertFalse(other.equals(null));
        for (Object o : other) {
            assertFalse(o.equals(null));
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void equalsItselfIdentity(final Object object, final Object same, final Object[] other) {
        assertEquals(object, object);
        assertEquals(same, same);
        for (Object o : other) {
            assertEquals(o, o);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void equalsItself(final Object object, final Object same, final Object[] other) {
        assertEquals(object, same);
        assertEquals(same, object);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void notEquals(final Object object, final Object same, final Object[] other) {
        for (Object o : other) {
            assertNotEquals(o, object);
            assertNotEquals(object, o);
            assertNotEquals(o, same);
            assertNotEquals(same, o);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void notEqualsType(final Object object, final Object same, final Object[] other) {
        assertNotEquals(object, OTHER_TYPE);
        assertNotEquals(OTHER_TYPE, object);
        assertNotEquals(other, OTHER_TYPE);
        assertNotEquals(OTHER_TYPE, other);
        for (Object o : other) {
            assertNotEquals(o, OTHER_TYPE);
            assertNotEquals(OTHER_TYPE, o);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void basicNoHashCollisions(final Object object, final Object same, final Object[] other) {
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
