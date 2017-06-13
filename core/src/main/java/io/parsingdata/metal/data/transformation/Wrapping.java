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

package io.parsingdata.metal.data.transformation;

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.transformation.Reversal.reverse;

import java.util.Optional;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.data.ImmutableList;

public final class Wrapping {

    private Wrapping() {}

    public static <T> ImmutableList<Optional<T>> wrap(final ImmutableList<T> list) {
        checkNotNull(list, "list");
        return reverse(wrap(list, new ImmutableList<>()).computeResult());
    }

    private static <T> SafeTrampoline<ImmutableList<Optional<T>>> wrap(final ImmutableList<T> input, final ImmutableList<Optional<T>> output) {
        if (input.isEmpty()) { return complete(() -> output); }
        return intermediate(() -> wrap(input.tail, output.add(Optional.of(input.head))));
    }

}
