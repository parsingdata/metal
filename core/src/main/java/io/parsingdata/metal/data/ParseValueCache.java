package io.parsingdata.metal.data;

import static io.parsingdata.metal.data.Selection.NO_LIMIT;
import static io.parsingdata.metal.data.Selection.reverse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

public class ParseValueCache {

    private final Map<String, ImmutableList<ParseValue>> cache;

    public ParseValueCache() {
        this(new HashMap<>());
    }

    public ParseValueCache(final Map<String, ImmutableList<ParseValue>> cache) {
        this.cache = cache;
    }

    public Optional<ImmutableList<Value>> find(final String scopeName, int limit) {
        final String s = shortName(scopeName);
        ImmutableList<ParseValue> valueImmutableList = cache.getOrDefault(s, new ImmutableList<>());
        ImmutableList<Value> result = new ImmutableList<>();
        while (valueImmutableList != null && (limit == NO_LIMIT || result.size < limit)) {
            final ParseValue head = valueImmutableList.head;
            if (head != null && head.matches(scopeName)) {
                result = result.add(head);
            }
            valueImmutableList = valueImmutableList.tail;
        }
        return Optional.of(reverse(result));
    }

    public ParseValueCache add(final ParseValue value) {
        final String name = shortName(value.name);
        final Map<String, ImmutableList<ParseValue>> stringImmutableListHashMap = new HashMap<>(cache);
        stringImmutableListHashMap.computeIfAbsent(name, pattern -> new ImmutableList<>());
        stringImmutableListHashMap.computeIfPresent(name, (pattern, valueImmutableList) -> valueImmutableList.add(value));
        return new ParseValueCache(stringImmutableListHashMap);
    }

    private static String shortName(final String name) {
        return name.substring(name.lastIndexOf(Token.SEPARATOR) + 1);
    }

    @Override
    public String toString() {
        return "size=" + cache.size();
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
