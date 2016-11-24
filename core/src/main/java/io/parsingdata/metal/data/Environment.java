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

import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class Environment {

    public final ParseGraph order;
    public final long offset;
    public final SourceFactory sourceFactory;
    public final Callbacks callbacks;

    public Environment(final ParseGraph order, final SourceFactory sourceFactory, final long offset, final Callbacks callbacks) {
        this.order = checkNotNull(order, "order");
        this.sourceFactory = checkNotNull(sourceFactory, "sourceFactory");
        this.offset = offset;
        this.callbacks = checkNotNull(callbacks, "callbacks");
    }

    public Environment(final ByteStream input, final long offset, final Callbacks callbacks) {
        this(ParseGraph.EMPTY, new ByteStreamSourceFactory(input), offset, callbacks);
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
        return new Environment(order.addBranch(token), sourceFactory, offset, callbacks);
    }

    public Environment closeBranch() {
        return new Environment(order.closeBranch(), sourceFactory, offset, callbacks);
    }

    public Environment add(final ParseValue parseValue) {
        return new Environment(order.add(parseValue), sourceFactory, offset, callbacks);
    }

    public Environment add(final ParseReference parseReference) {
        return new Environment(order.add(parseReference), sourceFactory, offset, callbacks);
    }

    public Environment seek(final long newOffset) {
        return new Environment(order, sourceFactory, newOffset, callbacks);
    }

    public Environment source(final ValueExpression dataExpression, final Environment environment, final Encoding encoding) {
        return new Environment(order, new DataExpressionSourceFactory(dataExpression, environment, encoding), 0L, callbacks);
    }

    public Source slice(final int size) {
        if (sourceFactory.hasAvailable(offset, size)) {
            return sourceFactory.create(offset, size);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "source: " + sourceFactory + "; offset: " + offset + "; order: " + order + "; callbacks: " + callbacks;
    }

}
