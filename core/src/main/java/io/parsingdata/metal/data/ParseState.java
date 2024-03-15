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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.format;
import static io.parsingdata.metal.data.ParseValueCache.NO_CACHE;
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.token.Token;

public class ParseState extends ImmutableObject {

    public final ParseGraph order;
    public final ParseValueCache cache;
    public final BigInteger offset;
    public final Source source;
    public final ImmutableList<ImmutablePair<Token, BigInteger>> iterations;
    public final ImmutableList<ParseReference> references;
    public final int scopeDepth;

    public ParseState(final ParseGraph order, final ParseValueCache cache, final Source source, final BigInteger offset, final ImmutableList<ImmutablePair<Token, BigInteger>> iterations, final ImmutableList<ParseReference> references, final int scopeDepth) {
        this.order = checkNotNull(order, "order");
        this.cache = checkNotNull(cache, "cache");
        this.source = checkNotNull(source, "source");
        this.offset = checkNotNegative(offset, "offset");
        this.iterations = checkNotNull(iterations, "iterations");
        this.references = checkNotNull(references, "references");
        this.scopeDepth = scopeDepth;
    }

    public static ParseState createFromByteStream(final ByteStream input, final BigInteger offset) {
        return new ParseState(ParseGraph.EMPTY, new ParseValueCache(), new ByteStreamSource(input), offset, new ImmutableList<>(), new ImmutableList<>(), 0);
    }

    public static ParseState createFromByteStream(final ByteStream input) {
        return createFromByteStream(input, ZERO);
    }

    public ParseState addBranch(final Token token) {
        return new ParseState(order.addBranch(token), cache, source, offset, token.isIterable() ? iterations.addHead(new ImmutablePair<>(token, ZERO)) : iterations, references, token.isScopeDelimiter() ? scopeDepth + 1 : scopeDepth);
    }

    public ParseState closeBranch(final Token token) {
        if (token.isIterable() && !iterations.head().left.equals(token)) {
            throw new IllegalStateException(format("Cannot close branch for iterable token %s. Current iteration state is for token %s.", token.name, iterations.head().left.name));
        }
        return new ParseState(order.closeBranch(), cache, source, offset, token.isIterable() ? iterations.tail() : iterations, references, token.isScopeDelimiter() ? scopeDepth - 1 : scopeDepth);
    }

    public ParseState add(final ParseReference parseReference) {
        return new ParseState(order, cache, source, offset, iterations, references.addHead(parseReference), scopeDepth);
    }

    public ParseState add(final ParseValue parseValue) {
        return new ParseState(order.add(parseValue), cache.add(parseValue), source, offset, iterations, references, scopeDepth);
    }

    public ParseState createCycle(final ParseReference parseReference) {
        return new ParseState(order.add(parseReference), cache, source, offset, iterations, references, scopeDepth);
    }

    public ParseState iterate() {
        return new ParseState(order, cache, source, offset, iterations.tail().addHead(new ImmutablePair<>(iterations.head().left, iterations.head().right.add(ONE))), references, scopeDepth);
    }

    public Optional<ParseState> seek(final BigInteger newOffset) {
        return newOffset.compareTo(ZERO) >= 0 ? Optional.of(new ParseState(order, cache, source, newOffset, iterations, references, scopeDepth)) : Optional.empty();
    }

    public ParseState withOrder(final ParseGraph order) {
        return new ParseState(order, NO_CACHE, source, offset, iterations, references, scopeDepth);
    }

    public ParseState withSource(final Source source) {
        return new ParseState(order, cache, source, ZERO, iterations, references, scopeDepth);
    }

    public Optional<Slice> slice(final BigInteger length) {
        return createFromSource(source, offset, length);
    }

    @Override
    public String toString() {
        final String iterationsString = iterations.isEmpty() ? "" : ";iterations:" + iterations;
        final String referencesString = references.isEmpty() ? "" : ";references:" + references;
        return getClass().getSimpleName() + "(source:" + source + ";offset:" + offset + ";order:" + order + iterationsString + referencesString + ";scopeDepth:" + scopeDepth + ";" + cache + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(order, ((ParseState)obj).order)
            && Objects.equals(cache, ((ParseState)obj).cache)
            && Objects.equals(offset, ((ParseState)obj).offset)
            && Objects.equals(source, ((ParseState)obj).source)
            && Objects.equals(iterations, ((ParseState)obj).iterations)
            && Objects.equals(references, ((ParseState)obj).references)
            && Objects.equals(scopeDepth, ((ParseState)obj).scopeDepth);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), order, cache, offset, source, iterations, references, scopeDepth);
    }

}
