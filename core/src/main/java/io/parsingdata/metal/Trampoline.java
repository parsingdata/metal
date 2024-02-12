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

package io.parsingdata.metal;

/**
 * Implements the concept of a trampoline, a mechanism that encodes a tail
 * recursive call into a lambda, which can then be returned (unwinding the
 * stack) and invoked. There is still some mutable local state in the
 * {@link #computeResult()} method required to perform the iteration, but the
 * client code can be fully immutable as a result.
 */
public interface Trampoline<T> {

    T result();
    boolean hasNext();
    Trampoline<T> next();

    default T computeResult() {
        Trampoline<T> current = this;
        while (current.hasNext()) {
            current = current.next();
        }
        return current.result();
    }

    static <T> Trampoline<T> complete(final CompletedTrampoline<T> completedTrampoline) {
        return completedTrampoline;
    }

    @FunctionalInterface
    interface CompletedTrampoline<T> extends Trampoline<T> {

        @Override
        default boolean hasNext() {
            return false;
        }

        @Override
        default Trampoline<T> next() {
            throw new UnsupportedOperationException("A CompletedTrampoline does not have a next computation.");
        }

    }

    static <T> Trampoline<T> intermediate(final IntermediateTrampoline<T> intermediateTrampoline) {
        return intermediateTrampoline;
    }

    @FunctionalInterface
    interface IntermediateTrampoline<T> extends Trampoline<T> {

        @Override
        default T result() {
            throw new UnsupportedOperationException("An IntermediateTrampoline does not have a result.");
        }

        @Override
        default boolean hasNext() {
            return true;
        }

    }

}
