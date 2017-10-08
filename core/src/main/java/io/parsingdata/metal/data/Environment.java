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
import java.util.Optional;

import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class Environment {

    public final ParseGraph order;
    public final BigInteger offset;
    public final Source source;
    public final Callbacks callbacks;

    public Environment(final ParseGraph order, final Source source, final BigInteger offset, final Callbacks callbacks) {
        this.order = checkNotNull(order, "order");
        this.source = checkNotNull(source, "source");
        this.offset = checkNotNegative(offset, "offset");
        this.callbacks = checkNotNull(callbacks, "callbacks");
    }

    public Environment(final ByteStream input, final BigInteger offset, final Callbacks callbacks) {
        this(ParseGraph.EMPTY, new ByteStreamSource(input), offset, callbacks);
    }

    public Environment(final ByteStream input, final BigInteger offset) {
        this(input, offset, Callbacks.NONE);
    }

    public Environment(final ByteStream input, final Callbacks callbacks) {
        this(input, ZERO, callbacks);
    }

    public Environment(final ByteStream input) {
        this(input, ZERO);
    }

    public Environment addBranch(final Token token) {
        return new Environment(order.addBranch(token), source, offset, callbacks);
    }

    public Environment closeBranch() {
        return new Environment(order.closeBranch(), source, offset, callbacks);
    }

    public Environment add(final ParseValue parseValue) {
        return new Environment(order.add(parseValue), source, offset, callbacks);
    }

    public Environment add(final ParseReference parseReference) {
        return new Environment(order.add(parseReference), source, offset, callbacks);
    }

    public Optional<Environment> seek(final BigInteger newOffset) {
        return newOffset.compareTo(ZERO) >= 0 ? Optional.of(new Environment(order, source, newOffset, callbacks)) : Optional.empty();
    }

    public Environment source(final ValueExpression dataExpression, final int index, final Environment environment, final Encoding encoding) {
        return new Environment(order, new DataExpressionSource(dataExpression, index, environment.order, encoding), ZERO, callbacks);
    }

    public Optional<Slice> slice(final BigInteger length) {
        return createFromSource(source, offset, length);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(source:" + source + ";offset:" + offset + ";order:" + order + ";callbacks:" + callbacks + ")";
    }

}
