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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.ParseResult.failure;
import static io.parsingdata.metal.data.ParseResult.success;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link Token} that specifies an amount of data to skip in the input.
 * <p>
 * This tokens specifies a <code>size</code> (a {@link ValueExpression}) just
 * like {@link Def} and evaluates this in the same way (failing when not a
 * single value results). Unlike with Def, the data is not read but skipped
 * in the input.
 *
 * @see Def
 * @see ValueExpression
 */
public class Nod extends Token {

    public final ValueExpression size;

    public Nod(final String name, final ValueExpression size, final Encoding encoding) {
        super(name, encoding);
        this.size = checkNotNull(size, "size");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ImmutableList<Optional<Value>> sizes = size.eval(environment.order, encoding);
        if (sizes.size != 1 || !sizes.head.isPresent()) {
            return failure(environment);
        }
        final long skipSize = sizes.head.get().asNumeric().longValue();
        if (skipSize < 0) {
            return failure(environment);
        }
        return success(environment.seek(environment.offset + skipSize));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + size + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(size, ((Nod)obj).size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), size);
    }

}
