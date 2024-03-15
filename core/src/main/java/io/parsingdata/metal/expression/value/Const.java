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

package io.parsingdata.metal.expression.value;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link SingleValueExpression} representing a constant value.
 * <p>
 * Const has a single operand <code>value</code> (a {@link Value}). When
 * evaluated, this value is returned.
 */
public class Const extends ImmutableObject implements SingleValueExpression {

    public final Value value;

    public Const(final Value value) {
        this.value = value;
    }

    @Override
    public Optional<Value> evalSingle(final ParseState parseState, final Encoding encoding) {
        return Optional.of(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + value + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(value, ((Const)obj).value);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), value);
    }

}
