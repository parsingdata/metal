package io.parsingdata.metal.data;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.data.Selection.NO_LIMIT;
import static io.parsingdata.metal.data.Selection.reverse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

public class ParseValueCache {

    public static final ParseValueCache NO_CACHE = new ParseValueCache(null, null);

    private final Map<String, ImmutableList<ParseValue>> cache;
    private final Map<String, Token> tokenCache;

    /**
     * Start a cache that keeps track of values added to the parse graph.
     * <p>
     * In case no caching is desired, {@link #NO_CACHE} should be used instead.
     */
    public ParseValueCache() {
        this(new HashMap<>(), new HashMap<>());
    }

    // For internal use only. It is private to avoid setting the cache to null. The NO_CACHE constant should be used instead.
    private ParseValueCache(final Map<String, ImmutableList<ParseValue>> cache, final Map<String, Token> tokenCache) {
        this.cache = cache;
        this.tokenCache = tokenCache;
    }

    public Optional<ImmutableList<Value>> find(final String scopeName, int limit) {
        if (this == NO_CACHE) {
            return Optional.empty();
        }
        final ImmutableList<Value> result = find(cache.getOrDefault(shortName(scopeName), new ImmutableList<>()), scopeName, limit, new ImmutableList<>()).computeResult();
        return Optional.of(reverse(result));
    }

    private Trampoline<ImmutableList<Value>> find(final ImmutableList<ParseValue> searchList, final String scopeName, final int limit, final ImmutableList<Value> result) {
        if (searchList.isEmpty() || (limit != NO_LIMIT && result.size == limit)) {
            return complete(() -> result);
        }
        final ParseValue head = searchList.head;
        if (head.matches(scopeName)) {
            return intermediate(() -> find(searchList.tail, scopeName, limit, result.add(head)));
        }
        return intermediate(() -> find(searchList.tail, scopeName, limit, result));
    }

    public ParseValueCache add(final ParseValue value) {
        if (this == NO_CACHE) {
            return NO_CACHE;
        }
        final String name = shortName(value.name);
        final Map<String, ImmutableList<ParseValue>> newCache = new HashMap<>(cache);
        newCache.computeIfAbsent(name, pattern -> new ImmutableList<>());
        newCache.computeIfPresent(name, (pattern, valueImmutableList) -> valueImmutableList.add(value));
        return new ParseValueCache(newCache, tokenCache);
    }

    public ParseValueCache add(final Token token) {
        if (this == NO_CACHE) {
            return NO_CACHE;
        }
        final String name = token.name;
        if (name.isEmpty()) {
            return this;
        }
        final Map<String, Token> newTokenCache = new HashMap<>(tokenCache);
        newTokenCache.put(name, token);
        return new ParseValueCache(cache, newTokenCache);
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

    public Optional<Token> findToken(String referenceName) {
        System.out.println("ParseValueCache.findToken");
        return Optional.ofNullable(tokenCache.get(referenceName));
    }
}
