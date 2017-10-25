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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class ParseState {

    public final ParseGraph order;
    public final BigInteger offset;
    public final Source source;

    public ParseState(final ParseGraph order, final Source source, final BigInteger offset) {
        this.order = checkNotNull(order, "order");
        this.source = checkNotNull(source, "source");
        this.offset = checkNotNegative(offset, "offset");
    }

    public static ParseState createFromByteStream(final ByteStream input, final BigInteger offset) {
        return new ParseState(ParseGraph.EMPTY, new ByteStreamSource(input), offset);
    }

    public static ParseState createFromByteStream(final ByteStream input) {
        return createFromByteStream(input, ZERO);
    }

    public ParseState addBranch(final Token token) {
        return new ParseState(order.addBranch(token), source, offset);
    }

    public ParseState closeBranch() {
        return new ParseState(order.closeBranch(), source, offset);
    }

    public ParseState add(final ParseValue parseValue) {
        return new ParseState(order.add(parseValue), source, offset);
    }

    public ParseState add(final ParseReference parseReference) {
        return new ParseState(order.add(parseReference), source, offset);
    }

    public Optional<ParseState> seek(final BigInteger newOffset) {
        return newOffset.compareTo(ZERO) >= 0 ? Optional.of(new ParseState(order, source, newOffset)) : Optional.empty();
    }

    public ParseState source(final ValueExpression dataExpression, final int index, final ParseState parseState, final Encoding encoding) {
        return new ParseState(order, new DataExpressionSource(dataExpression, index, parseState.order, encoding), ZERO);
    }

    public Optional<Slice> slice(final BigInteger length) {
        return createFromSource(source, offset, length);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(source:" + source + ";offset:" + offset + ";order:" + order + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(order, ((ParseState)obj).order)
            && Objects.equals(offset, ((ParseState)obj).offset)
            && Objects.equals(source, ((ParseState)obj).source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().hashCode(), order, offset, source);
    }

}
