/*
 * Copyright 2013-2018 Netherlands Forensic Institute
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

import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;

import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Token;

/**
 * A {@link ValueExpression} that represents the 0-based current iteration in an
 * iterable {@link Token} (e.g. when inside a {@link Rep} or {@link RepN}).
 */
public class CurrentIteration implements ValueExpression {

    private final ValueExpression level;

    public CurrentIteration(final ValueExpression level) {
        this.level = level;
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final int level = getLevel(parseState, encoding);
        if (parseState.iterations.head == null) {
            return ImmutableList.create(Optional.empty());
        }
        return ImmutableList.create(Optional.of(createFromNumeric(parseState.iterations.head, new Encoding())));
    }

    private int getLevel(final ParseState parseState, final Encoding encoding) {
        ImmutableList<Optional<Value>> evaluatedLevel = level.eval(parseState, encoding);
        if (evaluatedLevel.size != 1 || !evaluatedLevel.head.isPresent()) {
            throw new IllegalArgumentException("Level must evaluate to a single non-empty value.");
        }
        return evaluatedLevel.head.get().asNumeric().intValueExact();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
                && Objects.equals(level, ((CurrentIteration)obj).level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), level);
    }

}
