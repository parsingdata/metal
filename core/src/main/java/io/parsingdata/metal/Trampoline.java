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

package io.parsingdata.metal;

import java.io.IOException;

public interface Trampoline<T> {

    T result();
    boolean hasNext();
    Trampoline<T> next() throws IOException;

    default T computeResult() throws IOException {
        Trampoline<T> current = this;
        while (current.hasNext()) {
            current = current.next();
        }
        return current.result();
    }

    static <T> Trampoline<T> complete(final CompletedTrampoline<T> completedTrampoline) { return completedTrampoline; }

    interface CompletedTrampoline<T> extends Trampoline<T> {

        default boolean hasNext() { return false; }
        default Trampoline<T> next() { throw new UnsupportedOperationException("A CompletedTrampoline does not have a next computation."); }

    }

    static <T> Trampoline<T> intermediate(final IntermediateTrampoline<T> intermediateTrampoline) { return intermediateTrampoline; }

    interface IntermediateTrampoline<T> extends Trampoline<T> {

        default T result() { throw new UnsupportedOperationException("An IntermediateTrampoline does not have a result."); }
        default boolean hasNext() { return true; }

    }

}
