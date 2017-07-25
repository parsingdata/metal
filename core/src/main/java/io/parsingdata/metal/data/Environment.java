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

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class Environment {

    public final ParseGraph order;
    public final long offset;
    public final Source source;
    public final Callbacks callbacks;

    public Environment(final ParseGraph order, final Source source, final long offset, final Callbacks callbacks) {
        this.order = checkNotNull(order, "order");
        this.source = checkNotNull(source, "source");
        this.offset = offset;
        this.callbacks = checkNotNull(callbacks, "callbacks");
    }

    public Environment(final ByteStream input, final long offset, final Callbacks callbacks) {
        this(ParseGraph.EMPTY, new ByteStreamSource(input), offset, callbacks);
    }

    public Environment(final ByteStream input, final long offset) {
        this(input, offset, Callbacks.NONE);
    }

    public Environment(final ByteStream input, final Callbacks callbacks) {
        this(input, 0L, callbacks);
    }

    public Environment(final ByteStream input) {
        this(input, 0L);
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

    public Environment seek(final long newOffset) {
        return new Environment(order, source, newOffset, callbacks);
    }

    public Environment source(final ValueExpression dataExpression, final int index, final Environment environment, final Encoding encoding) {
        return new Environment(order, new DataExpressionSource(dataExpression, index, environment.order, encoding), 0L, callbacks);
    }

    public Slice slice(final int size) throws IOException {
        return source.slice(offset, size);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(source:" + source + ";offset:" + offset + ";order:" + order + ";callbacks:" + callbacks + ")";
    }

}
