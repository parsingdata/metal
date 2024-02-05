package io.parsingdata.metal.data;

import static io.parsingdata.metal.data.Selection.NO_LIMIT;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

public class ParseValueCache {

    public static final ParseValueCache NO_CACHE = new ParseValueCache(null);

    private final Map<String, ImmutableList<ParseValue>> cache;

    /**
     * Start a cache that keeps track of values added to the parse graph.
     * <p>
     * In case no caching is desired, {@link #NO_CACHE} should be used instead.
     */
    public ParseValueCache() {
        this(new HashMap<>());
    }

    // For internal use only. It is private to avoid setting the cache to null. The NO_CACHE constant should be used instead.
    private ParseValueCache(final Map<String, ImmutableList<ParseValue>> cache) {
        this.cache = cache;
    }

    public Optional<ImmutableList<Value>> find(final String scopeName, int limit) {
        if (this == NO_CACHE) {
            return Optional.empty();
        }
        final ImmutableList<Value> result = find(cache.getOrDefault(shortName(scopeName), new ImmutableList<>()), scopeName, limit);
        return Optional.of(result);
    }

    private ImmutableList<Value> find(final ImmutableList<ParseValue> searchList, final String scopeName, final int limit) {
        Stream<ParseValue> collect = searchList.stream().filter(head -> head.matches(scopeName));
        if (limit != NO_LIMIT) {
            collect = collect.limit(limit);
        }
        return new ImmutableList<>(new LinkedList<>(collect.collect(Collectors.toList())));
    }

    public ParseValueCache add(final ParseValue value) {
        if (this == NO_CACHE) {
            return NO_CACHE;
        }
        final String name = shortName(value.name);
        final Map<String, ImmutableList<ParseValue>> stringImmutableListHashMap = new HashMap<>(cache);
        stringImmutableListHashMap.computeIfAbsent(name, pattern -> new ImmutableList<>());
        stringImmutableListHashMap.computeIfPresent(name, (pattern, valueImmutableList) -> valueImmutableList.addHead(value));
        return new ParseValueCache(stringImmutableListHashMap);
    }

    private static String shortName(final String name) {
        return name.substring(name.lastIndexOf(Token.SEPARATOR) + 1);
    }

    @Override
    public String toString() {
        if (this == NO_CACHE) {
            return "no-cache";
        }
        return "cache:size=" + cache.size();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(cache, ((ParseValueCache)obj).cache);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cache);
    }
}
