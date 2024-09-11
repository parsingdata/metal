package io.parsingdata.metal.data;

import static io.parsingdata.metal.Shorthand.CURRENT_ITERATION;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.Shorthand.when;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static java.math.BigInteger.ZERO;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.ParseValueCache.NO_CACHE;
import static io.parsingdata.metal.data.Selection.NO_LIMIT;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

class ParseValueCacheTest {

    private static final Slice SMALL_SLICE = createFromBytes(new byte[]{1, 2});
    private static ParseValueCache parseValueCache;
    private static ParseValue pv1;
    private static ParseValue pv2;
    private static ParseValue pv3;
    private static Token pv2Definition;
    private static Token pv3Definition;
    private static ParseGraph parseGraph;

    @BeforeAll
    public static void setup() {
        pv1 = parseValue("only.first.name");
        final ParseValue pvother = parseValue("something.else");
        pv2Definition = def("name", 2);
        pv2 = parseValue("first.second.name", pv2Definition);
        final ParseValue pvother2 = parseValue("name.not.last");
        pv3Definition = def("name", 3);
        pv3 = parseValue("second.second.name", pv3Definition);
        final ParseValue pvother3 = parseValue("other.subname");
        parseValueCache = new ParseValueCache().add(pv1).add(pvother).add(pv2).add(pvother2).add(pv3).add(pvother3);

        // Build a parseGraph with a scopeDepth of 2.
        final Token t = any("t");
        final Token s = seq("scopeDelimiter", t, t);
        parseGraph = ParseGraph.EMPTY.addBranch(s).addBranch(s);
    }

    public static Stream<Arguments> findTest() {
        return Stream.of(
            arguments("", List.of()),
            arguments("name", List.of(pv3, pv2, pv1)),
            arguments("first.name", List.of(pv1)),
            arguments("second.name", List.of(pv3, pv2)),
            arguments("second.second.name", List.of(pv3)),
            arguments("random.name", List.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void findTest(final String scopeName, final List<ParseValue> values) {
        final Optional<ImmutableList<Value>> nameValues = parseValueCache.find(scopeName, NO_LIMIT);
        assertTrue(nameValues.isPresent());
        nameValues.ifPresent(result -> {
            assertEquals(values.size(), result.size);
            ImmutableList<Value> tail = result;
            for (int i = 0; i < values.size(); i++) {
                assertNotNull(tail);
                assertEquals(values.get(i), tail.head);
                tail = tail.tail;
            }
        });
    }

    @ParameterizedTest
    @ValueSource(ints={0, 1, 2, 3, 4})
    public void limitTest(final int limit) {
        final Optional<ImmutableList<Value>> nameValues = parseValueCache.find("name", limit);
        assertTrue(nameValues.isPresent());
        nameValues.ifPresent(result -> {
            assertEquals(Math.min(limit, 3), result.size);
            if (limit > 0) assertEquals(pv3, result.head);
            if (limit > 1) assertEquals(pv2, result.tail.head);
            if (limit > 2) assertEquals(pv1, result.tail.tail.head);
        });
    }

    private static ParseValue parseValue(final String name) {
        return parseValue(name, NONE);
    }

    private static ParseValue parseValue(final String scopeName, final Token definition) {
        return new ParseValue(scopeName, definition, SMALL_SLICE, enc());
    }

    @Test
    public void noCacheTest() {
        final ParseValueCache noCache = NO_CACHE;
        assertFalse(noCache.find("randomName", NO_LIMIT).isPresent());
        final ParseValue pv1 = parseValue("name");
        final ParseValueCache newCache = noCache.add(pv1);
        assertFalse(newCache.find("name", NO_LIMIT).isPresent());
        assertEquals(NO_CACHE, newCache);
    }

    @Test
    public void addTest() {
        final ParseValueCache parseValueCache = new ParseValueCache();
        final ParseValue pv = parseValue("name");
        final ParseValueCache parseValueCache2 = parseValueCache.add(pv);

        final Optional<ImmutableList<Value>> nameValues = parseValueCache.find("name", NO_LIMIT);
        assertTrue(nameValues.isPresent());
        nameValues.ifPresent(result -> assertEquals(0, result.size));

        final Optional<ImmutableList<Value>> nameValues2 = parseValueCache2.find("name", NO_LIMIT);
        assertTrue(nameValues2.isPresent());
        nameValues2.ifPresent(result -> {
            assertEquals(1, result.size);
            assertEquals(pv, result.head);
        });
    }

    @Test
    public void toStringTest() {
        assertEquals("cache:size=4", parseValueCache.toString());
        assertEquals("no-cache", NO_CACHE.toString());
    }

    public static Stream<Arguments> cacheUsageTest() {
        return Stream.of(
            arguments("nameRef", ref("second.name"), true),
            arguments("nameRef with limit", ref(con(1), "second.name"), true),

            arguments("multi nameRef", ref("second.name", "first.name"), false),
            arguments("multi nameRef with limit", ref(con(1), "second.name", "first.name"), false),
            arguments("definitionRef", ref(pv2Definition), false),
            arguments("definitionRef with limit", ref(con(1), pv2Definition), false),
            arguments("multi definitionRef", ref(pv2Definition, pv3Definition), false),
            arguments("multi definitionRef with limit", ref(con(1), pv2Definition, pv3Definition), false),

            // Requested scope is smaller than the scopeDepth of the ParseGraph.
            arguments("scoped nameRef", scope(ref("second.name"), con(1)), false),
            arguments("scoped nameRef with limit", scope(ref(con(1), "second.name"), con(1)), false),
            arguments("scoped multi nameRef", scope(ref("second.name", "first.name"), con(1)), false),
            arguments("scoped multi nameRef with limit", scope(ref(con(1), "second.name", "first.name"), con(1)), false),
            arguments("scoped definitionRef", scope(ref(pv2Definition), con(1)), false),
            arguments("scoped definitionRef with limit", scope(ref(con(1), pv2Definition), con(1)), false),
            arguments("scoped multi definitionRef", scope(ref(pv2Definition, pv3Definition), con(1)), false),
            arguments("scoped multi definitionRef with limit", scope(ref(con(1), pv2Definition, pv3Definition), con(1)), false),

            // Requested scope matches or exceeds the scopeDepth of the ParseGraph.
            arguments("matching scoped nameRef", scope(ref("second.name"), con(2)), true),
            arguments("matching scoped nameRef with limit", scope(ref(con(1), "second.name"), con(2)), true),
            arguments("matching scoped multi nameRef", scope(ref("second.name", "first.name"), con(2)), false),
            arguments("matching scoped multi nameRef with limit", scope(ref(con(1), "second.name", "first.name"), con(2)), false),
            arguments("matching scoped definitionRef", scope(ref(pv2Definition), con(2)), false),
            arguments("matching scoped definitionRef with limit", scope(ref(con(1), pv2Definition), con(2)), false),
            arguments("matching scoped multi definitionRef", scope(ref(pv2Definition, pv3Definition), con(2)), false),
            arguments("matching scoped multi definitionRef with limit", scope(ref(con(1), pv2Definition, pv3Definition), con(2)), false)
        );
    }

    @ParameterizedTest(name="{2} - {0}")
    @MethodSource
    public void cacheUsageTest(final String testName, final ValueExpression expression, final boolean shouldUseCache) {
        final ParseState parseState = new ParseState(parseGraph, parseValueCache, createFromBytes(new byte[0]).source, ZERO, new ImmutableList<>(), new ImmutableList<>());
        final ImmutableList<Value> eval = expression.eval(parseState, enc());
        // Only the cache is filled with the values we are referring to.
        // That means, if result is not empty, the cache was used.
        assertEquals(shouldUseCache, !eval.isEmpty());
    }

    // Note: This timeout does not stop the test after 1 second.
    // The test will run until it finishes and then validate the duration.
    @Timeout(value = 50)
    @Test
    void performanceTest() {
        // This test would take way too much time without tokenref caching (~17sec).
        // Using tokenref cashing, these are all finished within less than 100 ms.
        final int dataSize = 1_000_000;
        final byte[] input = new byte[dataSize + 2 + 3];
        // This token contains recursive tokens to create large ParseGraphs.
        final Token deep =
            seq(
                seq("tokenref",
                    def("data1", 1),
                    def("data2", 1)
                ),
                rep("token",
                    seq("seq",
                        seq(
                            def("byte", 1),
                            nod(0)
                        ),
                        when(token("tokenref"), eqNum(CURRENT_ITERATION, con(dataSize)))
                    )
                )
            );
        final Optional<ParseState> result = deep.parse(env(createFromByteStream(new InMemoryByteStream(input))));
        assertTrue(result.isPresent());

        ImmutableList<ParseValue> allValues = Selection.getAllValues(result.get().order, x -> true);
        assertThat(allValues.size, equalTo(dataSize + 5L));
    }
}