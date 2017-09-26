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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Util.checkNotEmpty;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link Token} that specifies a value to parse in the input.
 * <p>
 * A Def consists of a <code>size</code> (a {@link ValueExpression}.
 * <p>
 * Parsing will succeed if <code>size</code> evaluates to a single value and if
 * that many bytes are available in the input. This means that a size of zero
 * will lead to a successful parse, but will not produce a value.
 *
 * @see ValueExpression
 */
public class Def extends Token {

    public final ValueExpression size;

    public Def(final String name, final ValueExpression size, final Encoding encoding) {
        super(checkNotEmpty(name, "name"), encoding);
        this.size = checkNotNull(size, "size");
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) {
        final ImmutableList<Optional<Value>> sizes = size.eval(environment.order, encoding);
        if (sizes.size != 1 || !sizes.head.isPresent()) {
            return failure();
        }
        final BigInteger dataSize = sizes.head.get().asNumeric();
        if (dataSize.compareTo(ZERO) == 0) {
            return success(environment);
        }
        if (!environment.isAvailable(dataSize)) {
            return failure();
        }
        return success(environment.add(new ParseValue(scope, this, environment.slice(dataSize), encoding)).seek(dataSize.add(BigInteger.valueOf(environment.offset)).longValue()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + size + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(size, ((Def)obj).size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), size);
    }

}
