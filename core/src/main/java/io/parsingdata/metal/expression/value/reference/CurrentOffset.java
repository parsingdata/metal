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

package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;
import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;

import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.SingleValueExpression;
import io.parsingdata.metal.expression.value.Value;

/**
 * A {@link SingleValueExpression} that represents the current offset in the
 * {@link ParseState}.
 */
public class CurrentOffset extends ImmutableObject implements SingleValueExpression {

    @Override
    public Optional<Value> evalSingle(final ParseState parseState, final Encoding encoding) {
        return Optional.of(createFromNumeric(parseState.offset, DEFAULT_ENCODING));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj);
    }

    @Override
    public int immutableHashCode() {
        return getClass().hashCode();
    }

}
