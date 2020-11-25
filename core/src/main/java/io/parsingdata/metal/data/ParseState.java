/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.token.Token;

public class ParseState {

    public final ParseGraph order;
    public final BigInteger offset;
    public final Source source;
    public final ImmutableList<ImmutablePair<Token, BigInteger>> iterations;
    public final ImmutableList<ParseReference> references;

    public ParseState(final ParseGraph order, final Source source, final BigInteger offset, final ImmutableList<ImmutablePair<Token, BigInteger>> iterations, final ImmutableList<ParseReference> references) {
        this.order = checkNotNull(order, "order");
        this.source = checkNotNull(source, "source");
        this.offset = checkNotNegative(offset, "offset");
        this.iterations = checkNotNull(iterations, "iterations");
        this.references = checkNotNull(references, "references");
    }

    public static ParseState createFromByteStream(final ByteStream input, final BigInteger offset) {
        return new ParseState(ParseGraph.EMPTY, new ByteStreamSource(input), offset, new ImmutableList<>(), new ImmutableList<>());
    }

    public static ParseState createFromByteStream(final ByteStream input) {
        return createFromByteStream(input, ZERO);
    }

    public ParseState addBranch(final Token token) {
        return new ParseState(order.addBranch(token), source, offset, token.isIterable() ? iterations.add(new ImmutablePair<>(token, ZERO)) : iterations, references);
    }

    public ParseState closeBranch(final Token token) {
        if (token.isIterable() && !iterations.head.left.equals(token)) {
            throw new IllegalStateException(format("Cannot close branch for iterable token %s. Current iteration state is for token %s.", token.name, iterations.head.left.name));
        }
        return new ParseState(order.closeBranch(), source, offset, token.isIterable() ? iterations.tail : iterations, references);
    }

    public ParseState add(final ParseValue parseValue) {
        return new ParseState(order.add(parseValue), source, offset, iterations, references);
    }

    public ParseState add(final ParseReference parseReference) {
        return new ParseState(order.add(parseReference), source, offset, iterations, references);
    }

    public ParseState iterate() {
        return new ParseState(order, source, offset, iterations.tail.add(new ImmutablePair<>(iterations.head.left, iterations.head.right.add(ONE))), references);
    }

    public Optional<ParseState> seek(final BigInteger newOffset) {
        return newOffset.compareTo(ZERO) >= 0 ? Optional.of(new ParseState(order, source, newOffset, iterations, references)) : Optional.empty();
    }

    public ParseState withOrder(final ParseGraph order) {
        return new ParseState(order, source, offset, iterations, references);
    }

    public ParseState withSource(final Source source) {
        return new ParseState(order, source, ZERO, iterations, references);
    }

    public ParseState withTargets(final ImmutableList<ParseReference> references) {
        return new ParseState(order, source, offset, iterations, references);
    }

    public Optional<Slice> slice(final BigInteger length) {
        return createFromSource(source, offset, length);
    }

    @Override
    public String toString() {
        final String iterationString = iterations.isEmpty() ? "" : ";iterations:" + iterations.toString();
        final String targetsString = references.isEmpty() ? "" : ";targets:" + references.toString();
        return getClass().getSimpleName() + "(source:" + source + ";offset:" + offset + ";order:" + order + iterationString + targetsString + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(order, ((ParseState)obj).order)
            && Objects.equals(offset, ((ParseState)obj).offset)
            && Objects.equals(source, ((ParseState)obj).source)
            && Objects.equals(iterations, ((ParseState)obj).iterations)
            && Objects.equals(references, ((ParseState)obj).references);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), order, offset, source, iterations, references);
    }

}
