package io.parsingdata.metal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.ParseValueCache.NO_CACHE;
import static io.parsingdata.metal.data.Selection.NO_LIMIT;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.expression.value.Value;

class ParseValueCacheTest {

    private static final Slice SMALL_SLICE = createFromBytes(new byte[]{1, 2});
    private static ParseValueCache parseValueCache;
    private static ParseValue pv1;
    private static ParseValue pv2;
    private static ParseValue pv3;

    @BeforeAll
    public static void setup() {
        pv1 = valueWithName("only.first.name");
        final ParseValue pvother = valueWithName("something.else");
        pv2 = valueWithName("first.second.name");
        final ParseValue pvother2 = valueWithName("name.not.last");
        pv3 = valueWithName("second.second.name");
        final ParseValue pvother3 = valueWithName("other.subname");
        parseValueCache = new ParseValueCache().add(pv1).add(pvother).add(pv2).add(pvother2).add(pv3).add(pvother3);
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

    @Test
    public void limitTest() {
        IntStream.range(0, 5).forEach(limit -> {
            final Optional<ImmutableList<Value>> nameValues = parseValueCache.find("name", limit);
            assertTrue(nameValues.isPresent());
            nameValues.ifPresent(result -> {
                assertEquals(Math.min(limit, 3), result.size);
                if (limit > 0) assertEquals(pv3, result.head);
                if (limit > 1) assertEquals(pv2, result.tail.head);
                if (limit > 2) assertEquals(pv1, result.tail.tail.head);
            });
        });
    }

    private static ParseValue valueWithName(String name) {
        return new ParseValue(name, NONE, SMALL_SLICE, enc());
    }

    @Test
    public void noCacheTest() {
        final ParseValueCache noCache = NO_CACHE;
        assertFalse(noCache.find("randomName", NO_LIMIT).isPresent());
        final ParseValue pv1 = valueWithName("name");
        final ParseValueCache newCache = noCache.add(pv1);
        assertFalse(newCache.find("name", NO_LIMIT).isPresent());
        assertEquals(NO_CACHE, newCache);
    }

    @Test
    public void addTest() {
        final ParseValueCache parseValueCache = new ParseValueCache();
        final ParseValue pv = valueWithName("name");
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
}